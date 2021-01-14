package ru.navilab.chessdevtest;

import org.junit.jupiter.api.*;
import ru.navilab.chessdevtest.impl.ChessDevTestImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ChessDevJUnitTest {
    private Random random = new Random();
    public static final String FIRST_CASE = "first case";
    public static final String SECOND_CASE = "second case";
    private ChessDevTestImpl test;

    private static class Item {
        int index;
        byte[] bytes;

        public Item(int index, byte[] bytes) {
            this.index = index;
            this.bytes = bytes;
        }
    }

    @BeforeAll
    public void before() {
        test = ChessDevTestImpl.createDefault();
        test.init();
    }

    @AfterAll
    public void after() {
        test.close();
    }

    @Test
    @Order(1)
    public void writeOnlyTest() {
        testWriteString(test, FIRST_CASE);
        testWriteString(test, SECOND_CASE);
    }

    @Test
    @Order(2)
    public void readOnlyTest() {
        testReadString(test, FIRST_CASE, 1);
        testReadString(test, SECOND_CASE, 2);
    }

    @Test
    @Order(3)
    public void uppend3Test() {
        testWriteString(test, SECOND_CASE + FIRST_CASE);
    }

    @Test
    @Order(4)
    public void read3Test() {
        testReadString(test, SECOND_CASE + FIRST_CASE, 3);
    }

    @Test
    @Order(5)
    public void bigTest() throws InterruptedException {
        ScheduledExecutorService executor = (ScheduledExecutorService) Executors.newScheduledThreadPool(16);
        for (int i = 0; i < 100; i++) {
            executor.schedule(() -> runLongSaveGet(test), 2, TimeUnit.SECONDS);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
    }

    private void runLongSaveGet(ChessDevTestImpl test) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            byte[] buffer = randomBuffer(10000);
            int index = test.save(buffer);
            list.add(new Item(index, buffer));
        }
        test.clearCache();
        Collections.shuffle(list);
        for (Item item : list) {
            byte[] bytes = test.get(item.index);
            Assertions.assertArrayEquals(item.bytes, bytes);
        }
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
