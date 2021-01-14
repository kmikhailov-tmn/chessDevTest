package ru.navilab.chessdevtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.navilab.chessdevtest.impl.ChessDevTestImpl;


public class ChessDevJUnitTest {

    @Test
    public void writeOnlyTest() {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        String inputStream = "first case";
        int index = test.save(inputStream.getBytes());
        byte[] bytes = test.get(index);
        String outString = new String(bytes);
        Assertions.assertEquals(inputStream, outString);
        test.close();
    }
}
