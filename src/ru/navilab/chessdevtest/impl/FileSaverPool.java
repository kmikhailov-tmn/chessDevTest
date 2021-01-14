package ru.navilab.chessdevtest.impl;

import java.util.List;

public interface FileSaverPool {
    FileSaver getFreeFileSaver();

    void closeAll();

    List<FileSaver> getAllFileSavers();
}
