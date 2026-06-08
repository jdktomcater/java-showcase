package com.jdktomcater.showcase.job.provider.util;

import lombok.extern.slf4j.Slf4j;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

@Slf4j
public final class IpUtils {

    private IpUtils() {
    }

    /**
     * 获取本机 IPv4 地址，优先返回非 loopback、非 docker 网卡上的地址。
     */
    public static String getLocalIp() {
        String ip = "127.0.0.1";
        try {
            for (Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces(); interfaces.hasMoreElements(); ) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (!networkInterface.isUp() || networkInterface.isLoopback()) {
                    continue;
                }
                String name = networkInterface.getName();
                if (name.contains("docker") || name.contains("lo") || name.contains("veth")) {
                    continue;
                }
                for (Enumeration<InetAddress> addresses = networkInterface.getInetAddresses(); addresses.hasMoreElements(); ) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address && !address.isLoopbackAddress() && !address.isLinkLocalAddress()) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            log.error("获取本地 IP 异常", ex);
        }
        return ip;
    }
}
