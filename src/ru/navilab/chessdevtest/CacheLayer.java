package ru.navilab.chessdevtest;

public interface CacheLayer {
    byte[] get(int index);

    void put(int index, byte[] buffer);
}
