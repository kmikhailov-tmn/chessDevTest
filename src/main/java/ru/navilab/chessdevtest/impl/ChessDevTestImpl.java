package ru.navilab.chessdevtest.impl;

import ru.navilab.chessdevtest.CacheLayer;
import ru.navilab.chessdevtest.ChessDevTest;
import ru.navilab.chessdevtest.Indexer;
import ru.navilab.chessdevtest.PersistLayer;

public class ChessDevTestImpl implements ChessDevTest {
    private PersistLayer persistLayer;
    private CacheLayer cacheLayer;
    private Indexer indexer = new Indexer();

    private ChessDevTestImpl(PersistLayer persistLayer, CacheLayer cacheLayer) {
        this.persistLayer = persistLayer;
        this.cacheLayer = cacheLayer;
    }

    @Override
    public int load() {
        int maxIndex = persistLayer.load();
        int nextIndex = maxIndex + 1;
        indexer.init(nextIndex);
        return nextIndex;
    }

    @Override
    public void close() {
        persistLayer.close();
    }

    @Override
    public void clearCache() {
        cacheLayer.clear();
    }

    public final static ChessDevTestImpl createDefault() {
        return new ChessDevTestImpl(new SimpleFilePersistLayer(), new SimpleMapCacheLayer());
    }

    public final static ChessDevTestImpl createCustom(PersistLayer persistLayer, CacheLayer cacheLayer) {
        return new ChessDevTestImpl(persistLayer, cacheLayer);
    }

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
        if (result == null) {
            result = persistLayer.get(index);
            cacheLayer.put(index, result);
        }
        return result;
    }
}
