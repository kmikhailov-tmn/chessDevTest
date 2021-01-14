package ru.navilab.chessdevtest.impl;

import java.io.IOException;

public class PersistLayerException extends RuntimeException {
    public PersistLayerException(IOException e) {
        super(e);
    }
}
