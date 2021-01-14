package ru.navilab.chessdevtest.impl;

public class TimeCounter {
    private long timeStamp = System.currentTimeMillis();

    public synchronized boolean isElapsed(long millis) {
        long now = System.currentTimeMillis();
        return (now - timeStamp) >= millis;
    }

    public synchronized void reset() {
        timeStamp = System.currentTimeMillis();
    }
}
