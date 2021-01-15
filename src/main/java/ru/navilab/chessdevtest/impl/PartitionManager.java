package ru.navilab.chessdevtest.impl;

import java.util.List;

public interface PartitionManager {
    IndexSaver getIndexSaver();

    void closeAll();

    List<? extends Partition> getAllPartitions();

    IndexReader getIndexReader(int index);
}
