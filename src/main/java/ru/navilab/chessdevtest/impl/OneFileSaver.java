package ru.navilab.chessdevtest.impl;

import java.util.Arrays;
import java.util.List;

public class OneFileSaver implements FileSaverPool {
    private FileSaver fileSaver;
    private FileSaverFactory factory;
    private FileSaver secondSaver;

    public OneFileSaver(FileSaverFactory factory) {
        this.factory = factory;
    }

    @Override
    public synchronized FileSaver getFreeFileSaver() {
        if (fileSaver == null) initSaver0();
        if (fileSaver.isReady()) return fileSaver;
        if (secondSaver == null) initSaver1();
        return fileSaver;
    }

    @Override
    public void closeAll() {
        if (fileSaver != null) fileSaver.close();
    }

    @Override
    public List<FileSaver> getAllFileSavers() {
        initSaver0();
        initSaver1();
        return Arrays.asList(fileSaver, secondSaver);
    }

    private void initSaver1() {
        secondSaver = factory.createFileSaver(1);
    }

    private void initSaver0() {
        fileSaver = factory.createFileSaver(0);
    }
}
