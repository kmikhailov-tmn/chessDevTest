package ru.navilab.chessdevtest.impl;

import java.util.Arrays;
import java.util.List;

public class TwoPartitionsManager implements PartitionManager {
    private FileSaver fileSaver;
    private FileSaver secondSaver;
    private FileSaverFactory factory;

    public TwoPartitionsManager(FileSaverFactory factory) {
        this.factory = factory;
    }

    @Override
    public synchronized IndexSaver getIndexSaver() {
        if (fileSaver == null) initSaver0();
        if (fileSaver.isReady()) return fileSaver;
        if (secondSaver == null) initSaver1();
        return secondSaver;
    }

    @Override
    public void closeAll() {
        if (fileSaver != null) fileSaver.close();
        if (secondSaver != null) secondSaver.close();
    }

    @Override
    public List<? extends Partition> getAllPartitions() {
        initSaver0();
        initSaver1();
        return getAllFileSavers();
    }

    @Override
    public IndexReader getIndexReader(int index) {
        List<FileSaver> fileSavers = getAllFileSavers();
        for (FileSaver saver : fileSavers) {
            IndexReader indexReader = saver.getIndexReader(index);
            if (indexReader != null) return indexReader;
        }
        throw new PersistLayerException("index absent in FileSavers " + index);
    }

    private List<FileSaver> getAllFileSavers() {
        return Arrays.asList(fileSaver, secondSaver);
    }

    private void initSaver1() {
        secondSaver = factory.createFileSaver(1);
    }

    private void initSaver0() {
        fileSaver = factory.createFileSaver(0);
    }
}
