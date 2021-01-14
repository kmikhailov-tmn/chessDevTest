package ru.navilab.chessdevtest;

public class SimpleMapCacheLayer implements CacheLayer {
    @Override
    public byte[] get(int index) {
        return new byte[0];
    }

    @Override
    public void put(int index, byte[] buffer) {

    }
}
