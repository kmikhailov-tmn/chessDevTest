package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.PersistLayer;

public class SimpleFilePersistLayer implements PersistLayer {
    private FileSaverPool fileSaverPool = new OneFileSaver((index) -> { return new FileSaver(index); });

    @Override
    public void save(int index, byte[] buffer) {
        FileSaver fileSaver = fileSaverPool.getFreeFileSaver();
        fileSaver.save(index, buffer);
    }

    @Override
    public byte[] get(int index) {
        FileSaver fileSaver = fileSaverPool.getFreeFileSaver();
        return fileSaver.get(index);
    }

    @Override
    public void load() {

    }

    @Override
    public int getMaxIndex() {
        return 0;
    }
}
