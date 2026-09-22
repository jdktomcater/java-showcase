package com.jdktomcat.showcase.cloud.repro;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.transport.EurekaHttpClient;
import com.netflix.discovery.shared.transport.decorator.RedirectingEurekaHttpClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.cloud.netflix.eureka.RestTemplateTimeoutProperties;
import org.springframework.cloud.netflix.eureka.http.DefaultEurekaClientHttpRequestFactorySupplier;
import org.springframework.cloud.netflix.eureka.http.RestTemplateEurekaHttpClient;
import org.springframework.cloud.netflix.eureka.http.RestTemplateTransportClientFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.HttpServerErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Real Spring Cloud transport, local HTTP endpoint, no running registry required.
 * These characterization tests intentionally expect the 4.1.0 defect.
 */
@Timeout(45)
class Eureka4103Test {
    private static final int CYCLES = 5;
    private static final InstanceInfo INSTANCE = InstanceInfo.Builder.newBuilder()
            .setAppName("ISSUE4103").setInstanceId("local-probe")
            .setHostName("localhost").setIPAddr("127.0.0.1")
            .setStatus(InstanceInfo.InstanceStatus.UP).build();

    @Test
    void normalHeartbeatsReuseOneConnection() throws Exception {
        try (Peer peer = new Peer(); Probe probe = new Probe(peer, false)) {
            for (int i = 0; i < 12; i++) {
                probe.heartbeat();
            }
            assertEquals(1, probe.factories.size(), "No failure should recreate the client");
            assertEquals(1, peer.accepted.get(), "Healthy heartbeats should reuse one TCP connection");
            assertEquals(1, peer.open.size());
            probe.client.shutdown();
            assertEquals(1, probe.shutdowns.get());
            assertEquals(1, peer.open.size(), "4.1.0 shutdown does not close its HttpClient");
            report("normal", peer, probe);
            probe.closeUnderlyingClients();
            await(() -> peer.open.isEmpty(), "Explicit cleanup did not close the baseline socket");
            peer.assertHealthy();
        }
    }

    @Test
    void heartbeatFailuresLeaveOldClientsOpen() throws Exception {
        runFailureCycles(false);
    }

    @Test
    void closingOwnedHttpClientPreventsAccumulation() throws Exception {
        runFailureCycles(true);
    }

    private void runFailureCycles(boolean closeOnShutdown) throws Exception {
        try (Peer peer = new Peer(); Probe probe = new Probe(peer, closeOnShutdown)) {
            for (int i = 1; i <= CYCLES; i++) {
                peer.status = 200;
                probe.heartbeat();
                peer.status = 500;
                assertThrows(HttpServerErrorException.class, probe::heartbeat);
                // The real RedirectingEurekaHttpClient handles the exception and calls shutdown.
                assertEquals(i, probe.shutdowns.get());
                assertEquals(i, probe.factories.size());
                assertEquals(i, peer.accepted.get());
                if (closeOnShutdown) {
                    await(() -> peer.open.isEmpty(), "Control client did not release its socket");
                } else {
                    assertEquals(i, peer.open.size(), "Each abandoned pool should retain a connection");
                }
                System.out.printf("cycle=%d closeOnShutdown=%s clients=%d shutdownCalls=%d openSockets=%d%n",
                        i, closeOnShutdown, probe.factories.size(), probe.shutdowns.get(), peer.open.size());
            }
            // Send FIN without advertising Connection: close in earlier responses.
            // Retired clients never read again; their sockets can now enter CLOSE_WAIT.
            peer.halfClose();
            int expectedOpen = closeOnShutdown ? 0 : CYCLES;
            if (!closeOnShutdown) {
                Thread.sleep(300);
                assertEquals(expectedOpen, peer.open.size());
            }
            report(closeOnShutdown ? "explicit-close-control" : "default-failure", peer, probe);
            probe.closeUnderlyingClients();
            await(() -> peer.open.isEmpty(), "Final cleanup did not release all retired clients");
            peer.assertHealthy();
        }
    }

    private static void report(String name, Peer peer, Probe probe) throws Exception {
        String summary = String.format(
                "scenario=%s netflix=%s eureka=%s httpclient=%s clients=%d shutdownCalls=%d accepted=%d open=%d%n",
                name, RestTemplateEurekaHttpClient.class.getPackage().getImplementationVersion(),
                EurekaHttpClient.class.getPackage().getImplementationVersion(),
                org.apache.hc.client5.http.impl.classic.HttpClients.class.getPackage().getImplementationVersion(),
                probe.factories.size(), probe.shutdowns.get(), peer.accepted.get(), peer.open.size());
        System.out.print(summary);
        Path reportDir = Path.of("target", "issue-4103");
        Files.createDirectories(reportDir);
        Files.writeString(reportDir.resolve(name + ".txt"), summary);
        if (System.getProperty("os.name").startsWith("Windows")) {
            Process process = new ProcessBuilder("netstat", "-ano", "-p", "tcp")
                    .redirectErrorStream(true).start();
            String output = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            assertTrue(process.waitFor(5, TimeUnit.SECONDS), "netstat did not finish");
            assertEquals(0, process.exitValue(), output);
            String port = ":" + peer.listener.getLocalPort();
            List<String> relevant = output.lines().filter(line -> line.contains(port))
                    .filter(line -> line.trim().endsWith(" " + ProcessHandle.current().pid())).toList();
            Files.write(reportDir.resolve(name + "-netstat.txt"), relevant);
            long closeWait = relevant.stream().filter(line -> line.contains("CLOSE_WAIT")).count();
            System.out.println("OS CLOSE_WAIT=" + closeWait + " (loopback port " + peer.listener.getLocalPort() + ")");
            if (name.equals("default-failure")) {
                assertEquals(CYCLES, closeWait, "Expected client-side CLOSE_WAIT after the peer sends FIN");
            } else {
                assertEquals(0, closeWait);
            }
        }
    }

    private static void await(BooleanSupplier condition, String message) throws InterruptedException {
        long deadline = System.nanoTime() + Duration.ofSeconds(5).toNanos();
        while (!condition.getAsBoolean() && System.nanoTime() < deadline) {
            Thread.sleep(20);
        }
        assertTrue(condition.getAsBoolean(), message);
    }

    private static final class Probe implements AutoCloseable {
        // Strong references make observation deterministic before GC; all are destroyed in close().
        final List<HttpComponentsClientHttpRequestFactory> factories = new ArrayList<>();
        final AtomicInteger shutdowns = new AtomicInteger();
        final RestTemplateTransportClientFactory factory;
        final RedirectingEurekaHttpClient client;

        Probe(Peer peer, boolean closeOnShutdown) {
            RestTemplateTimeoutProperties timeouts = new RestTemplateTimeoutProperties();
            timeouts.setConnectTimeout(2000);
            timeouts.setConnectRequestTimeout(2000);
            timeouts.setSocketTimeout(2000);
            var supplier = new DefaultEurekaClientHttpRequestFactorySupplier(timeouts);
            factory = new RestTemplateTransportClientFactory(Optional.empty(), Optional.empty(), (ssl, verifier) -> {
                var requestFactory = (HttpComponentsClientHttpRequestFactory) supplier.get(ssl, verifier);
                factories.add(requestFactory);
                return requestFactory;
            }) {
                @Override
                public EurekaHttpClient newClient(com.netflix.discovery.shared.resolver.EurekaEndpoint endpoint) {
                    EurekaHttpClient delegate = super.newClient(endpoint);
                    HttpComponentsClientHttpRequestFactory ownedFactory = factories.get(factories.size() - 1);
                    return (EurekaHttpClient) Proxy.newProxyInstance(EurekaHttpClient.class.getClassLoader(),
                            new Class<?>[]{EurekaHttpClient.class}, (proxy, method, args) -> {
                                try {
                                    if (method.getName().equals("shutdown")) {
                                        shutdowns.incrementAndGet();
                                        try {
                                            return method.invoke(delegate, args);
                                        } finally {
                                            if (closeOnShutdown) {
                                                // Only this experimental control changes lifecycle handling.
                                                ownedFactory.destroy();
                                            }
                                        }
                                    }
                                    return method.invoke(delegate, args);
                                } catch (InvocationTargetException e) {
                                    throw e.getCause();
                                }
                            });
                }
            };
            // DNS is never used: the peer emits no redirects and the endpoint is a literal loopback IP.
            client = new RedirectingEurekaHttpClient(peer.url(), factory,
                    new com.netflix.discovery.shared.dns.DnsServiceImpl());
        }

        void heartbeat() {
            assertEquals(200, client.sendHeartBeat("ISSUE4103", "local-probe", INSTANCE, null).getStatusCode());
        }

        void closeUnderlyingClients() throws Exception {
            for (HttpComponentsClientHttpRequestFactory requestFactory : factories) {
                requestFactory.destroy();
            }
        }

        @Override
        public void close() throws Exception {
            try {
                client.shutdown();
                factory.shutdown();
            } finally {
                closeUnderlyingClients();
            }
        }
    }

    /** Minimal persistent HTTP peer with explicit FIN control and observable client EOF. */
    private static final class Peer implements AutoCloseable {
        final ServerSocket listener;
        final Set<Socket> open = ConcurrentHashMap.newKeySet();
        final AtomicInteger accepted = new AtomicInteger();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final ExecutorService workers = Executors.newCachedThreadPool(runnable -> {
            Thread thread = new Thread(runnable, "issue4103-peer");
            thread.setDaemon(true);
            return thread;
        });
        volatile int status = 200;
        volatile boolean stopped;

        Peer() throws IOException {
            listener = new ServerSocket(0, 16, InetAddress.getByName("127.0.0.1"));
            workers.submit(() -> {
                try {
                    while (!stopped) {
                        Socket socket = listener.accept();
                        open.add(socket);
                        accepted.incrementAndGet();
                        workers.submit(() -> serve(socket));
                    }
                } catch (IOException e) {
                    if (!stopped) {
                        failure.compareAndSet(null, e);
                    }
                }
            });
        }

        String url() {
            return "http://127.0.0.1:" + listener.getLocalPort() + "/eureka/";
        }

        void serve(Socket socket) {
            try (socket; var reader = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.US_ASCII))) {
                String request;
                while ((request = reader.readLine()) != null) {
                    if (!request.startsWith("PUT /eureka/apps/ISSUE4103/local-probe?")) {
                        throw new IOException("Unexpected request: " + request);
                    }
                    String header;
                    int length = 0;
                    while ((header = reader.readLine()) != null && !header.isEmpty()) {
                        if (header.regionMatches(true, 0, "Content-Length:", 0, 15)) {
                            length = Integer.parseInt(header.substring(15).trim());
                        }
                    }
                    for (int i = 0; i < length; i++) {
                        if (reader.read() == -1) {
                            throw new IOException("Truncated request");
                        }
                    }
                    int response = status;
                    String headers = "HTTP/1.1 " + response + (response == 200 ? " OK" : " Internal Server Error")
                            + "\r\nContent-Length: 0\r\nConnection: keep-alive\r\n\r\n";
                    socket.getOutputStream().write(headers.getBytes(StandardCharsets.US_ASCII));
                    socket.getOutputStream().flush();
                }
            } catch (IOException e) {
                if (!stopped) {
                    failure.compareAndSet(null, e);
                }
            } finally {
                open.remove(socket);
            }
        }

        void halfClose() throws IOException {
            for (Socket socket : open) {
                socket.shutdownOutput();
            }
        }

        void assertHealthy() {
            assertNull(failure.get(), () -> "Local peer failed: " + failure.get());
        }

        @Override
        public void close() throws IOException, InterruptedException {
            stopped = true;
            listener.close();
            for (Socket socket : open) {
                socket.close();
            }
            workers.shutdownNow();
            assertTrue(workers.awaitTermination(5, TimeUnit.SECONDS), "Peer workers did not stop");
        }
    }
}
