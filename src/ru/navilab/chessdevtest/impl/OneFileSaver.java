package ru.navilab.chessdevtest.impl;

import java.util.function.Function;

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
}
