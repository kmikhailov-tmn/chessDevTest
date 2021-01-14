package ru.navilab.chessdevtest;

public class Indexer {
    private int index;

    public synchronized int nextIndex() {
        return index++;
    }

    /**
     * init indexer to start with nextIndex
     * @param nextIndex
     * thread unsafe, should be called in TestImplementation only
     */
    public void init(int nextIndex) {
        index = nextIndex;
    }
}
