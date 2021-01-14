package ru.navilab.chessdevtest.impl;

public class TimestampedBytes {
    private byte[] bytes;
    private volatile long timestamp;

    public TimestampedBytes(long timestamp, byte[] bytes) {
        this.bytes = bytes;
        this.timestamp = timestamp;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public synchronized void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
