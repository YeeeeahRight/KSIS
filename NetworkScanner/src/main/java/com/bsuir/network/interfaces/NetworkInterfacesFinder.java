package com.bsuir.network.interfaces;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class NetworkInterfacesFinder {

    public static List<NetworkInterface> getCorrectInterfaces() throws SocketException {
        List<NetworkInterface> networkInterfaces = new ArrayList<>();
        Enumeration<NetworkInterface> interfaceEnumeration = NetworkInterface.getNetworkInterfaces();
        while (interfaceEnumeration.hasMoreElements()) {
            NetworkInterface currentInterface = interfaceEnumeration.nextElement();
            if (currentInterface.isUp() && !currentInterface.isLoopback()) {
                networkInterfaces.add(currentInterface);
            }
        }
        return networkInterfaces;
    }
}
