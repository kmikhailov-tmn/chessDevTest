package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.PersistLayer;

import java.util.List;

public class SimpleFilePersistLayer implements PersistLayer {
    public static final String BASE_STORAGE_DIR = ".";
    private PartitionManager partitionManager = new TwoPartitionsManager((index) -> { return new FileSaver(index); });

    @Override
    public void save(int index, byte[] buffer) {
        IndexSaver indexSaver = partitionManager.getIndexSaver();
        indexSaver.save(index, buffer);
    }

    @Override
    public byte[] get(int index) {
        IndexReader indexReader = partitionManager.getIndexReader(index);
        return indexReader.readBytes();
    }

    @Override
    public int load() {
        int maxIndex = 0;
        List<? extends Partition> allPartions = partitionManager.getAllPartitions();
        for (Partition partition: allPartions) {
            int index = partition.loadIndices();
            if (index > maxIndex) maxIndex = index;
        }
        return maxIndex;
    }

    @Override
    public void close() {
        partitionManager.closeAll();
    }
}
