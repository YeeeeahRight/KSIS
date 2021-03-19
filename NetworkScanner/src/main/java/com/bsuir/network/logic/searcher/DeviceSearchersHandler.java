package com.bsuir.network.logic.searcher;

import com.bsuir.network.command.CommandLine;
import com.bsuir.network.entity.IP;
import org.apache.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class DeviceSearchersHandler {
    private static final Logger LOGGER = Logger.getLogger(DeviceSearchersHandler.class);
    private static final int SEARCHERS_AMOUNT = 10;

    private final AtomicLong devicesCounter;
    private final String networkIP;
    private final String broadcastIP;

    public DeviceSearchersHandler(String networkIP, String broadcastIP) {
        devicesCounter = new AtomicLong(0);
        this.networkIP = networkIP;
        this.broadcastIP = broadcastIP;
    }

    public void handle(long hostsAmount) {
        System.out.println("Max devices: " + hostsAmount);
        int[] hostAmounts = getHostAmounts(hostsAmount);
        IP[] IPs = getIPs(networkIP, hostAmounts);
        CommandLine.executeCommand("arp refresh");
        ExecutorService executorService = Executors.newFixedThreadPool(SEARCHERS_AMOUNT);
        System.out.println("\n~~~~~Scanning devices~~~~~\n");
        for (int i = 0; i < SEARCHERS_AMOUNT; i++) {
            Thread searcher = new Thread(new DeviceSearcher(hostAmounts[i], IPs[i], networkIP,
                    broadcastIP, devicesCounter));
            executorService.submit(searcher);
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(15, TimeUnit.MINUTES)) {
                LOGGER.warn("Searching was more than 15 minutes(too long). ");
            }
        } catch (InterruptedException e) {
            LOGGER.debug(e.getMessage(), e);
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
        }
        return IPs;
    }
}
