package com.bsuir.network.entity;

public class IP {
    public static final short BYTES_AMOUNT = 4;
    public static final short MAX_BYTE_SIZE = 255;
    private String IPStr;

    public IP(String IPStr) {
        this.IPStr = IPStr;
    }

    public IP(short[] IPBytes) {
        this.IPStr = convertIPBytesToString(IPBytes);
    }

    public String getIPStr() {
        return IPStr;
    }

    public void increment() {
        short[] IPBytes = convertStringToIPBytes(IPStr);

        for (int i = BYTES_AMOUNT - 1; i >= 0; i--) {
            if (IPBytes[i] != MAX_BYTE_SIZE) {
                IPBytes[i]++;
                break;
            } else {
                IPBytes[i] = 0;
            }
        }

        this.IPStr = convertIPBytesToString(IPBytes);
    }

    public static String convertIPBytesToString(short[] IPBytes) {
        StringBuilder IPString = new StringBuilder();
        for (int i = 0; i < BYTES_AMOUNT; i++) {
            if (i != 0) {
                IPString.append(".");
            }
            IPString.append(IPBytes[i]);
        }
        return IPString.toString();
    }

    public static short[] convertStringToIPBytes(String IPStr) {
        short[] IPBytes = new short[BYTES_AMOUNT];
        int counter = 0;
        for (String IPByteStr : IPStr.split("\\.")) {
            IPBytes[counter++] = Short.parseShort(IPByteStr);
        }
        return IPBytes;
    }
}
