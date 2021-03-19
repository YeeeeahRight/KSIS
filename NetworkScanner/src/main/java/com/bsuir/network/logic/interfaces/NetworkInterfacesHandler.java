package com.bsuir.network.logic.interfaces;

import com.bsuir.network.entity.IP;
import com.bsuir.network.logic.searcher.DeviceSearchersHandler;
import org.apache.log4j.Logger;

import java.math.BigInteger;
import java.net.*;
import java.util.Enumeration;
import java.util.List;

public class NetworkInterfacesHandler {
    private static final Logger LOGGER = Logger.getLogger(NetworkInterfacesHandler.class);
    private final List<NetworkInterface> networkInterfaces;

    public NetworkInterfacesHandler(List<NetworkInterface> networkInterfaces) {
        this.networkInterfaces = networkInterfaces;
    }

    public void handle() {
        int interfaceCounter = 1;
        for (NetworkInterface networkInterface : networkInterfaces) {
            printInterfaceInfo(networkInterface, interfaceCounter++);
            InterfaceAddress interfaceAddress = networkInterface.getInterfaceAddresses().get(0);
            short bitMask = interfaceAddress.getNetworkPrefixLength();
            short[] networkMask = findNetworkMask(bitMask);
            System.out.printf("Mask: %d.%d.%d.%d\n", networkMask[0], networkMask[1], networkMask[2], networkMask[3]);
            short[] networkIP = findNetworkIP(interfaceAddress, networkMask);
            String networkIPStr = networkIP[0] + "." + networkIP[1] + "." + networkIP[2] + "." + networkIP[3];
            System.out.println("Network IP: " + networkIPStr);
            String broadcastIP = interfaceAddress.getBroadcast().getHostAddress();
            System.out.println("Broadcast IP: " + broadcastIP);
            DeviceSearchersHandler deviceSearchersHandler = new DeviceSearchersHandler(networkIPStr, broadcastIP);
            long hostsAmount = (int)Math.pow(2, 32 - bitMask) - 2;
            deviceSearchersHandler.handle(hostsAmount);
        }
        if (interfaceCounter == 1) {
            System.out.println("There are no network interfaces to scan :(");
        }
    }

    private void printInterfaceInfo(NetworkInterface networkInterface, int interfaceCounter) {
        System.out.printf("Network interface #%d:\n", interfaceCounter);
        System.out.printf("Display name: %s\n", networkInterface.getDisplayName());
        System.out.printf("Name: %s\n", networkInterface.getName());
        Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();
        while (inetAddresses.hasMoreElements()) {
            System.out.printf("InetAddress: %s\n", inetAddresses.nextElement());
        }
        try {
            String macStr = convertMacToStr(networkInterface.getHardwareAddress());
            System.out.println("MAC address: " + macStr);
        } catch (SocketException e) {
            LOGGER.warn("Problem occurs during getting MAC address of network interface.", e);
        }
    }

    private String convertMacToStr(byte[] address) {
        String mac = new BigInteger(1, address).toString(16);
        mac = String.format("%12s", mac);
        mac = mac.replaceAll(" ", "0");
        StringBuilder stringBuilder = new StringBuilder();
        int counter = 0;
        for (Character character : mac.toCharArray()) {
            stringBuilder.append(character);
            counter++;
            if (counter != 0 && counter != 12 && counter % 2 == 0) {
                stringBuilder.append("-");
            }
        }
        return stringBuilder.toString();
    }

    private short[] findNetworkMask(short bitMask) {
        short[] networkMask = new short[IP.BYTES_AMOUNT];
        networkMask[0] = buildMaskByte(1, bitMask);
        networkMask[1] = buildMaskByte(2, bitMask);
        networkMask[2] = buildMaskByte(3, bitMask);
        networkMask[3] = buildMaskByte(4, bitMask);

        return networkMask;
    }

    private short buildMaskByte(int byteNum, short bitMask) {
        byte bitLength;
        if (bitMask - byteNum * 8 >= 0) {
            bitLength = 8;
        } else {
            bitLength = (byte) (8 - (byteNum * 8 - bitMask));
        }
        short networkByte = 0;
        for (int i = 0; i < 8; i++) {
            if (i < bitLength) {
                networkByte++;
            }
            if (i != 7) {
                networkByte <<= 1;
            }
        }
        return networkByte;
    }

    private short[] findNetworkIP(InterfaceAddress networkInterface, short[] networkMask) {
        String hostIpStr = networkInterface.getAddress().getHostAddress();
        short[] hostIp = convertIPStrToArr(hostIpStr);
        short[] networkIp = new short[IP.BYTES_AMOUNT];
        for (int i = 0; i < IP.BYTES_AMOUNT; i++) {
            networkIp[i] = (short) (hostIp[i] & networkMask[i]);
        }

        return networkIp;
    }

    private short[] convertIPStrToArr(String ipStr) {
        short[] ip = new short[IP.BYTES_AMOUNT];
        ipStr += '.';
        for (int i = 0; i < IP.BYTES_AMOUNT; i++) {
            int dotIndex = ipStr.indexOf('.');
            ip[i] = Short.parseShort(ipStr.substring(0, dotIndex));
            ipStr = ipStr.substring(dotIndex + 1);
        }
        return ip;
    }
}
