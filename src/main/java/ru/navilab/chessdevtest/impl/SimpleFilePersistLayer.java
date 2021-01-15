package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.PersistLayer;

import java.util.List;

public class SimpleFilePersistLayer implements PersistLayer {
    public static final String BASE_STORAGE_DIR = ".";
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
    public int load() {
        int maxIndex = 0;
        List<FileSaver> fileSaverList = fileSaverPool.getAllFileSavers();
        for (FileSaver saver : fileSaverList) {
            saver.load();
            int index = saver.getMaxIndex();
            if (index > maxIndex) maxIndex = index;
        }
        return maxIndex;
    }

    @Override
    public void close() {
        fileSaverPool.closeAll();
    }
}
