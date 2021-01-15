package ru.navilab.chessdevtest;

/**
 * Задание:
 * сделать класс с методами int save(byte[] buffer); и byte[] get(int index);.
 * При этом сохранять и забирать могут много потоков и с разной нагрузкой.
 * Нужно предусмотреть сохранение на диск, чтобы после перезагрузки данные не терялись.
 * требуется предусмотреть автотест своего решения
 */
public interface ChessDevTest {
    int save(byte[] buffer);
    byte[] get(int index);

    /**
     * Used to load the state of the test if saved data exist
     * Warning: not thread safe, call only once  in one thread after constructing the class and before invoking
     * save & get
     * @return maxIndex - maximum index number
     */
    int load();

    /**
     * Used to close all file savers
     */
    void close();

    void clearCache();
}
