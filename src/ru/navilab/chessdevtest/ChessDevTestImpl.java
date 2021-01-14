package ru.navilab.chessdevtest;

public class ChessDevTestImpl implements ChessDevTest {
    private PersistLayer persistLayer;
    private CacheLayer cacheLayer;
    private Indexer indexer;

    private ChessDevTestImpl(PersistLayer persistLayer, CacheLayer cacheLayer, Indexer indexer) {
        this.persistLayer = persistLayer;
        this.cacheLayer = cacheLayer;
        this.indexer = indexer;
    }

    public final static ChessDevTestImpl createDefault() {
        return new ChessDevTestImpl(new SimpleFilePersistLayer(), new SimpleMapCacheLayer(), new SimpleIndexer());
    }

    public final static ChessDevTestImpl createCustom(PersistLayer persistLayer, CacheLayer cacheLayer, Indexer indexer) {
        return new ChessDevTestImpl(persistLayer, cacheLayer, indexer);
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
