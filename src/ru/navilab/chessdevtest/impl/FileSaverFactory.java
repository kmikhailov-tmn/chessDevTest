package ru.navilab.chessdevtest.impl;

@FunctionalInterface
public interface FileSaverFactory {
    FileSaver createFileSaver(int fileIndex);
}
