package com.bsuir.network.command;

import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandLine {
    private static final Logger LOGGER = Logger.getLogger(CommandLine.class);

    public static String executeARPCommand(String addr) {
        executeCommand("arp refresh");
        String command = "arp -a " + addr;
        String answer = executeCommand(command);
        Matcher matcher = Pattern.compile("([\\da-fA-F]{2}-?){6}").matcher(answer);

        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public static String executeCommand(String command) {
        StringBuilder answer = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader cmdReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line = cmdReader.readLine();
            while (line != null) {
                answer.append(line).append("\n");
                line = cmdReader.readLine();
            }
        } catch (IOException e) {
            LOGGER.warn("Problem occurs with reading answer from cmd.", e);
        }
        return answer.toString();
    }
}
