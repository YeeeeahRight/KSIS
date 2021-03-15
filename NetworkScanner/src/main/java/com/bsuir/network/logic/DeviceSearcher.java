package com.bsuir.network.logic;

import com.bsuir.network.command.CommandLine;
import com.bsuir.network.entity.IP;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CountDownLatch;

import java.util.concurrent.atomic.AtomicLong;

public class DeviceSearcher implements Runnable {
    public static final int TIMEOUT = 1000;

    private final long hostsAmount;
    private final IP ip;
    private final String networkIP;
    private final String broadcastIP;
    private final AtomicLong devicesCounter;
    private final CountDownLatch countDownLatch;

    public DeviceSearcher(long hostsAmount, IP ip, String networkIP, String broadcastIP,
                          AtomicLong devicesCounter, CountDownLatch countDownLatch) {
        this.hostsAmount = hostsAmount;
        this.ip = ip;
        this.networkIP = networkIP;
        this.broadcastIP = broadcastIP;
        this.devicesCounter = devicesCounter;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        for (long i = 0; i < hostsAmount; i++) {
            String ipStr = ip.getIPStr();
            if (ipStr.equals(networkIP) || ipStr.equals(broadcastIP)) {
                ip.increment();
                continue;
            }
            try {
                InetAddress.getByName(ipStr).isReachable(TIMEOUT);
                String macAddress = CommandLine.executeARPCommand(ipStr);
                if (macAddress != null) {
                    System.out.printf("Founded %d device:\n", devicesCounter.incrementAndGet());
                    System.out.println("Device MAC address: " + macAddress);
                    System.out.println("Device IP: " + ipStr);
                    System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
                }
            } catch (IOException e) {
                System.out.printf("Warning! Problem occurs during checking %s: %s", ipStr, e.getMessage());
            }
            ip.increment();
        }
        countDownLatch.countDown();
    }


}
