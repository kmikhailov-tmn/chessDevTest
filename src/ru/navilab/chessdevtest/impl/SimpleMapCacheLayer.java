package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.CacheLayer;

import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class SimpleMapCacheLayer implements CacheLayer {
    private static final long CACHE_TIME_LIMIT_MILLIS = 65000;
    private Map<Integer, TimestampedBytes> cacheMap = Collections.synchronizedMap(new Hashtable<>());
    private float cacheCleanTresholdPercent;
    private Logger logger = Logger.getLogger(this.getClass().getName());
    private Lock cleanLock = new ReentrantLock();

    public SimpleMapCacheLayer() {
        cacheCleanTresholdPercent = getFreeMemoryInPercent() / 2;
        logger.fine("cacheCleanTresholdPercent = " + cacheCleanTresholdPercent);
    }

    @Override
    public byte[] get(int index) {
        TimestampedBytes tsBytes = cacheMap.get(index);
        if (tsBytes != null) {
            tsBytes.setTimestamp(System.currentTimeMillis());
            return tsBytes.getBytes();
        }
        return null;
    }

    @Override
    public void put(int index, byte[] buffer) {
        cacheMap.put(index, new TimestampedBytes(System.currentTimeMillis(), buffer));
        if (isMemoryExhausted()) {
            if (cleanLock.tryLock()) {
                try {
                    new Thread(() -> cleanCache()).start();
                } finally {
                    cleanLock.unlock();
                }
            }
        }
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }

    private boolean isMemoryExhausted() {
        float percentFree = getFreeMemoryInPercent();
        return percentFree < cacheCleanTresholdPercent;
    }

    private float getFreeMemoryInPercent() {
        Runtime runtime = Runtime.getRuntime();
        return (float) runtime.freeMemory() / runtime.totalMemory() * 100;
    }

    private synchronized void cleanCache() {
        logger.fine("cleanCache getFreeMemoryInPercent()=" + getFreeMemoryInPercent());
        long currentTimeMillis = System.currentTimeMillis();
        List<Integer> removeList = new ArrayList<>();
        List<Map.Entry<Integer, TimestampedBytes>> entries = new ArrayList<>(cacheMap.entrySet());
        for (Map.Entry<Integer, TimestampedBytes> entry : entries) {
            TimestampedBytes value = entry.getValue();
            long timestamp = value.getTimestamp();
            if ((currentTimeMillis - timestamp) > CACHE_TIME_LIMIT_MILLIS) {
                removeList.add(entry.getKey());
            }
        }
        removeList.forEach(integer -> {
            cacheMap.remove(integer);
        });
    }
}
