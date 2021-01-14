package ru.navilab.chessdevtest;

import java.util.List;
import java.util.ListIterator;

public class ChessDevTestImpl implements ChessDevTest {
    private PersistLayer persistLayer = new SimpleFilePersistLayer();
    private CacheLayer cacheLayer = new SimpleMapCacheLayer();
    private Indexer indexer = new SimpleIndexer();

    @Override
    public int save(byte[] buffer) {
        int index = indexer.nextIndex();
        persistLayer.save(index, buffer);
        cacheLayer.put(index, buffer);
        return index;
    }

    @Override
    public byte[] get(int index) {
        byte[] result = cacheLayer.get(index);
        if (result == null) persistLayer.get(index);
        return result;
    }

    public void setPersistLayer(PersistLayer persistLayer) {
        this.persistLayer = persistLayer;
    }

    public void setCacheLayer(CacheLayer cacheLayer) {
        this.cacheLayer = cacheLayer;
    }

    public void setIndexer(Indexer indexer) {
        this.indexer = indexer;
    }
}
