package ru.navilab.chessdevtest.impl;

import java.util.Arrays;
import java.util.List;

public class OneFileSaver implements FileSaverPool {
    private FileSaver fileSaver;
    private FileSaverFactory factory;

    public OneFileSaver(FileSaverFactory factory) {
        this.factory = factory;
    }

    @Override
    public FileSaver getFreeFileSaver() {
        if (fileSaver == null) fileSaver = factory.createFileSaver(0);
        return fileSaver;
    }

    @Override
    public void closeAll() {
        if (fileSaver != null) fileSaver.close();
    }

    @Override
    public List<FileSaver> getAllFileSavers() {
        return Arrays.asList(getFreeFileSaver());
    }
}
