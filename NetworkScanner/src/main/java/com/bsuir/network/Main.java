package com.bsuir.network;

import com.bsuir.network.interfaces.NetworkInterfacesFinder;
import com.bsuir.network.interfaces.NetworkInterfacesHandler;

import java.net.*;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterfacesFinder.getCorrectInterfaces();
        } catch (SocketException e) {
            System.out.println("Warning: something wrong with getting network interfaces: " + e.getMessage());
        }
        if (networkInterfaces == null || networkInterfaces.size() == 0) {
            System.out.println("There are no network interfaces for scan");
            System.exit(0);
        }
        printLocalHostInfo();
        NetworkInterfacesHandler networkInterfacesHandler = new NetworkInterfacesHandler(networkInterfaces);
        networkInterfacesHandler.handle();
    }

    public static void printLocalHostInfo() {
        System.out.println("Local host info: ");
        try {
            InetAddress localHost = Inet4Address.getLocalHost();
            System.out.println("Name: " + localHost.getHostName());
            System.out.println("IP: " + localHost.getHostAddress());
        } catch (UnknownHostException e) {
            System.out.println("Warning! " + e.getMessage());
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
    }
}
