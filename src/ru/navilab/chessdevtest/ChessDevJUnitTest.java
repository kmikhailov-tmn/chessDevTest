package ru.navilab.chessdevtest;

import org.junit.jupiter.api.*;
import ru.navilab.chessdevtest.impl.ChessDevTestImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChessDevJUnitTest {
    private Random random = new Random();
    public static final String FIRST_CASE = "first case";
    public static final String SECOND_CASE = "second case";

    private static class Item {
        int index;
        byte[] bytes;

        public Item(int index, byte[] bytes) {
            this.index = index;
            this.bytes = bytes;
        }
    }

    @Test
    @Order(1)
    public void writeOnlyTest() {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        test.init();
        testWriteString(test, FIRST_CASE);
        testWriteString(test, SECOND_CASE);
        test.close();
    }

    @Test
    @Order(2)
    public void readOnlyTest() {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        test.init();
        testReadString(test, FIRST_CASE, 1);
        testReadString(test, SECOND_CASE, 2);
        test.close();
    }

    @Test
    @Order(3)
    public void uppend3Test() {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        test.init();
        testWriteString(test, SECOND_CASE + FIRST_CASE);
        test.close();
    }

    @Test
    @Order(4)
    public void read3Test() {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        test.init();
        testReadString(test, SECOND_CASE + FIRST_CASE, 3);
        test.close();
    }

    @Test
    @Order(5)
    public void bigTest() {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        test.init();

        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            byte[] buffer = randomBuffer(1000000);
            int index = test.save(buffer);
            list.add(new Item(index, buffer));
        }
//        test.clearCache();
        Collections.shuffle(list);
        for (Item item : list) {
            byte[] bytes = test.get(item.index);
            Assertions.assertArrayEquals(item.bytes, bytes);
        }
        test.close();
    }

    private byte[] randomBuffer(int bound) {
        return new byte[random.nextInt(bound)];
    }


    private void testWriteString(ChessDevTestImpl test, String inputStream) {
        int index = test.save(inputStream.getBytes());
        byte[] bytes = test.get(index);
        String outString = new String(bytes);
        Assertions.assertEquals(inputStream, outString);
    }

    private void testReadString(ChessDevTestImpl test, String string, int index) {
        byte[] bytes = test.get(index);
        String outString = new String(bytes);
        Assertions.assertEquals(string, outString);
    }
}
