package com.bsuir.network.logic.searcher;

import com.bsuir.network.command.CommandLine;
import com.bsuir.network.entity.IP;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceSearchersHandler {
    private static final int SEARCHERS_AMOUNT = 10;

    private final CountDownLatch countDownLatch;
    private final AtomicLong devicesCounter;
    private final String networkIP;
    private final String broadcastIP;

    public DeviceSearchersHandler(String networkIP, String broadcastIP) {
        countDownLatch = new CountDownLatch(SEARCHERS_AMOUNT);
        devicesCounter = new AtomicLong(0);
        this.networkIP = networkIP;
        this.broadcastIP = broadcastIP;
    }

    public void handle(long hostsAmount) {
        System.out.println("Max devices: " + hostsAmount);
        int[] hostAmounts = getHostAmounts(hostsAmount);
        IP[] IPs = getIPs(networkIP, hostAmounts);
        CommandLine.executeCommand("arp refresh");
        System.out.println("\n~~~~~Scanning devices~~~~~\n");
        for (int i = 0; i < SEARCHERS_AMOUNT; i++) {
            Thread searcher = new Thread(new DeviceSearcher(hostAmounts[i], IPs[i], networkIP,
                    broadcastIP, devicesCounter, countDownLatch));
            searcher.start();
        }
        try {
            if (!countDownLatch.await(10, TimeUnit.MINUTES)) {
                System.out.println();
            }
        } catch (InterruptedException ignore) {
            System.out.println("Some ");
        }
        System.out.println("\n~~~~Scanning done~~~~\n");
    }

    private int[] getHostAmounts(long hostsAmount) {
        int[] hostsAmounts = new int[SEARCHERS_AMOUNT];
        long hostsAmountLeft = hostsAmount;
        for (int i = 0; i < SEARCHERS_AMOUNT; i++) {
            long diff = hostsAmount / SEARCHERS_AMOUNT;
            hostsAmounts[i] = (int) (hostsAmountLeft - (hostsAmountLeft - diff));
            hostsAmountLeft -= diff;
        }
        if (hostsAmountLeft > 0) {
            hostsAmounts[SEARCHERS_AMOUNT - 1] += hostsAmountLeft;
        }
        //производителя mac-adres и имя
        return hostsAmounts;
    }

    private IP[] getIPs(String IPStr, int[] hostsAmounts) {
        IP[] IPs = new IP[SEARCHERS_AMOUNT];
        String prevIPStr = IPStr;
        for (int i = 0; i < SEARCHERS_AMOUNT; i++) {
            IP ip = new IP(prevIPStr);
            if (i != 0) {
                for (int j = 0; j < hostsAmounts[i]; j++) {
                    ip.increment();
                }
            }
            IPs[i] = ip;
            prevIPStr = ip.getIPStr();
            System.out.println(ip.getIPStr());
        }
        return IPs;
    }
}
