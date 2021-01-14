package ru.navilab.chessdevtest;

public interface PersistLayer {
    void save(int index, byte[] buffer);

    byte[] get(int index);

    /**
     * Load index data from file(s)
     * @return max index determined after load
     */
    int load();

    void close();
}
