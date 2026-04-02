package com.jdktomcat.showcase.ai.impact.analysis.service;

import com.jdktomcat.showcase.ai.impact.analysis.domain.EntryPoint;
import com.jdktomcat.showcase.ai.impact.analysis.domain.EntryPointType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for EntryPointScanner.
 */
class EntryPointScannerTest {

    @TempDir
    Path tempDir;

    private EntryPointScanner scanner;

    @BeforeEach
    void setUp() {
        scanner = new EntryPointScanner(tempDir.toString());
    }

    @Test
    void testScan_HttpEntryPoints() throws IOException {
        // Create a test controller file
        String controllerContent = """
                package com.example;
                
                import org.springframework.web.bind.annotation.*;
                
                @RestController
                @RequestMapping("/orders")
                public class OrderController {
                    
                    @PostMapping("/create")
                    public OrderResponse createOrder(@RequestBody OrderRequest request) {
                        return new OrderResponse();
                    }
                    
                    @GetMapping("/{id}")
                    public OrderResponse getOrder(@PathVariable Long id) {
                        return new OrderResponse();
                    }
                    
                    public void internalMethod() {
                        // Not an entry point
                    }
                }
                """;

        Path controllerFile = tempDir.resolve("OrderController.java");
        Files.writeString(controllerFile, controllerContent);

        List<EntryPoint> entryPoints = scanner.scan();

        assertEquals(2, entryPoints.size());
        
        EntryPoint createEndpoint = entryPoints.stream()
                .filter(ep -> ep.methodName().equals("createOrder"))
                .findFirst()
                .orElseThrow();
        
        assertEquals(EntryPointType.HTTP, createEndpoint.type());
        assertEquals("OrderController", createEndpoint.className());
    }

    @Test
    void testScan_ScheduledEntryPoints() throws IOException {
        String taskContent = """
                package com.example;
                
                import org.springframework.scheduling.annotation.Scheduled;
                import org.springframework.stereotype.Component;
                
                @Component
                public class CleanupTask {
                    
                    @Scheduled(cron = "0 0 2 * * ?")
                    public void cleanup() {
                        // Cleanup logic
                    }
                }
                """;

        Path taskFile = tempDir.resolve("CleanupTask.java");
        Files.writeString(taskFile, taskContent);

        List<EntryPoint> entryPoints = scanner.scan();

        assertEquals(1, entryPoints.size());
        
        EntryPoint scheduledEndpoint = entryPoints.get(0);
        assertEquals(EntryPointType.SCHEDULED, scheduledEndpoint.type());
        assertEquals("cleanup", scheduledEndpoint.methodName());
    }

    @Test
    void testScan_MqEntryPoints() throws IOException {
        String listenerContent = """
                package com.example;
                
                import org.springframework.amqp.rabbit.annotation.RabbitListener;
                import org.springframework.stereotype.Component;
                
                @Component
                public class OrderMessageListener {
                    
                    @RabbitListener(queues = "order.create")
                    public void handleOrderCreate(String message) {
                        // Handle message
                    }
                }
                """;

        Path listenerFile = tempDir.resolve("OrderMessageListener.java");
        Files.writeString(listenerFile, listenerContent);

        List<EntryPoint> entryPoints = scanner.scan();

        assertEquals(1, entryPoints.size());
        
        EntryPoint mqEndpoint = entryPoints.get(0);
        assertEquals(EntryPointType.MQ, mqEndpoint.type());
        assertEquals("handleOrderCreate", mqEndpoint.methodName());
    }

    @Test
    void testScan_EventEntryPoints() throws IOException {
        String listenerContent = """
                package com.example;
                
                import org.springframework.context.event.EventListener;
                import org.springframework.stereotype.Component;
                
                @Component
                public class OrderEventListener {
                    
                    @EventListener
                    public void handleOrderCreated(OrderCreatedEvent event) {
                        // Handle event
                    }
                }
                """;

        Path listenerFile = tempDir.resolve("OrderEventListener.java");
        Files.writeString(listenerFile, listenerContent);

        List<EntryPoint> entryPoints = scanner.scan();

        assertEquals(1, entryPoints.size());
        
        EntryPoint eventEndpoint = entryPoints.get(0);
        assertEquals(EntryPointType.EVENT, eventEndpoint.type());
        assertEquals("handleOrderCreated", eventEndpoint.methodName());
    }

    @Test
    void testScan_CustomAnnotations() throws IOException {
        String controllerContent = """
                package com.example;
                
                import com.jdktomcat.showcase.ai.impact.analysis.annotation.HttpEntry;
                
                public class OrderController {
                    
                    @HttpEntry(paths = {"/create"}, methods = {"POST"})
                    public OrderResponse createOrder(OrderRequest request) {
                        return new OrderResponse();
                    }
                }
                """;

        Path controllerFile = tempDir.resolve("OrderController.java");
        Files.writeString(controllerFile, controllerContent);

        List<EntryPoint> entryPoints = scanner.scan();

        assertEquals(1, entryPoints.size());
        
        EntryPoint httpEndpoint = entryPoints.get(0);
        assertEquals(EntryPointType.HTTP, httpEndpoint.type());
        assertTrue(httpEndpoint.metadata().contains("HttpEntry"));
    }

    @Test
    void testScan_IgnoresNonJavaFiles() throws IOException {
        // Create non-Java files
        Files.writeString(tempDir.resolve("README.md"), "# README");
        Files.writeString(tempDir.resolve("config.xml"), "<config></config>");

        List<EntryPoint> entryPoints = scanner.scan();

        assertTrue(entryPoints.isEmpty());
    }

    @Test
    void testScan_IgnoresInternalMethods() throws IOException {
        String controllerContent = """
                package com.example;
                
                import org.springframework.web.bind.annotation.*;
                
                @RestController
                public class OrderController {
                    
                    @PostMapping("/create")
                    public OrderResponse createOrder(OrderRequest request) {
                        validate(request);
                        return new OrderResponse();
                    }
                    
                    private void validate(OrderRequest request) {
                        // Internal method, not an entry point
                    }
                    
                    private OrderResponse buildResponse() {
                        // Internal method, not an entry point
                    }
                }
                """;

        Path controllerFile = tempDir.resolve("OrderController.java");
        Files.writeString(controllerFile, controllerContent);

        List<EntryPoint> entryPoints = scanner.scan();

        assertEquals(1, entryPoints.size());
        assertEquals("createOrder", entryPoints.get(0).methodName());
    }
}
