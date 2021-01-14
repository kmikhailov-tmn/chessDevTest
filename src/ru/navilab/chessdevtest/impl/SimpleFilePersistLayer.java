package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.PersistLayer;

public class SimpleFilePersistLayer implements PersistLayer {
    @Override
    public void save(int index, byte[] buffer) {

    }

    @Override
    public byte[] get(int index) {
        return null;
    }

    @Override
    public void load() {

    }

    @Override
    public int getMaxIndex() {
        return 0;
    }
}
