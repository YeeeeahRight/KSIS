package com.bsuir.network.application;

import com.bsuir.network.logic.interfaces.NetworkInterfacesFinder;
import com.bsuir.network.logic.interfaces.NetworkInterfacesHandler;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;

import java.net.*;
import java.util.List;

public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class);

    public static void main(String[] args) {
        BasicConfigurator.configure();
        List<NetworkInterface> networkInterfaces = null;
        try {
            networkInterfaces = NetworkInterfacesFinder.getCorrectInterfaces();
        } catch (SocketException e) {
            LOGGER.warn("Something wrong with getting network interfaces.", e);
        }
        if (networkInterfaces == null || networkInterfaces.size() == 0) {
            LOGGER.info("There are no network interfaces for scan.");
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
            LOGGER.warn("Something wrong with getting host info.", e);
        }
        System.out.println("~~~~~~~~~~~~~~~~~~~~~");
    }
}
