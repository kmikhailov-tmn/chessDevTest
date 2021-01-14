package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.CacheLayer;

public class SimpleMapCacheLayer implements CacheLayer {
    @Override
    public byte[] get(int index) {
        return new byte[0];
    }

    @Override
    public void put(int index, byte[] buffer) {

    }
}
