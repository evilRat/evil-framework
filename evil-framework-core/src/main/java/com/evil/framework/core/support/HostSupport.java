package com.evil.framework.core.support;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

/**
 * 获取主机的 ip和 hostName
 *
 * @author kongzheng
 * @since 1.0.0.RELEASE
 */
public final class HostSupport {

    private static String ip;

    private static String hostName;

    static {
        try {
            // Init the host information.
            resolveHost();
        } catch (Exception e) {
            // just ignore
        }
    }

    private static void resolveHost() throws Exception {
        InetAddress addr = InetAddress.getLocalHost();
        hostName = addr.getHostName();
        ip = addr.getHostAddress();
        if (addr.isLoopbackAddress()) {
            // find the first IPv4 Address that not loopback
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface in = interfaces.nextElement();
                Enumeration<InetAddress> addrs = in.getInetAddresses();
                while (addrs.hasMoreElements()) {
                    InetAddress address = addrs.nextElement();
                    if (!address.isLoopbackAddress() && address instanceof Inet4Address) {
                        ip = address.getHostAddress();
                    }
                }
            }
        }
    }

    public static String getIp() {
        return ip;
    }

    public static String getHostName() {
        return hostName;
    }

    private HostSupport() {}
}
