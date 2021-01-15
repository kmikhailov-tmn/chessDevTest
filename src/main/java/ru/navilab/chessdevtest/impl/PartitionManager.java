package ru.navilab.chessdevtest.impl;

import java.util.List;

public interface PartitionManager {
    /**
     * get first ready IndexSaver
     * @return
     */
    IndexSaver getIndexSaver();

    void closeAll();

    List<? extends Partition> getAllPartitions();

    IndexReader getIndexReader(int index);
}
