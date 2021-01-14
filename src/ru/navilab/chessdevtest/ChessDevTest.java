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
}
