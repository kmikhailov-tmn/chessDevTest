package ru.navilab.chessdevtest;

public interface PersistLayer {
    void save(int index, byte[] buffer);

    byte[] get(int index);

    void load();

    int getMaxIndex();
}
