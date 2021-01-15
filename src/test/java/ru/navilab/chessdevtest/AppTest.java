package ru.navilab.chessdevtest;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ru.navilab.chessdevtest.impl.ChessDevTestImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AppTest {
    private final Random random = new Random();
    private ChessDevTestImpl test;

    private static class Item {
        int index;
        byte[] bytes;

        public Item(int index, byte[] bytes) {
            this.index = index;
            this.bytes = bytes;
        }
    }

    /**
     * MultiThread тест сначала записывает много порций, потом читает их и сравнивает, в нескольких потоках
     * (кэш рандомно очищается в разных потоках, чтобы get иногда не только из кэша данные брал, но и читал с диска)
     * тут конечно надо бы TestNG
     */
    @Test
    public void bigTest() throws InterruptedException {
        test = ChessDevTestImpl.createDefault();
        test.init();
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);
        for (int i = 0; i < 100; i++) {
            executor.schedule(() -> runLongSaveGet(test), 2, TimeUnit.SECONDS);
        }
        executor.shutdown();
        executor.awaitTermination(1, TimeUnit.HOURS);
        test.close();
    }

    private void runLongSaveGet(ChessDevTestImpl test) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 500; i++) {
            byte[] buffer = randomBuffer(10000);
            int index = test.save(buffer);
            list.add(new Item(index, buffer));
        }
        if (random.nextInt(100) > 70) test.clearCache();
        Collections.shuffle(list);
        for (Item item : list) {
            byte[] bytes = test.get(item.index);
            Assert.assertArrayEquals(item.bytes, bytes);
        }
    }

    private byte[] randomBuffer(int bound) {
        byte[] bytes = new byte[random.nextInt(bound)];
        random.nextBytes(bytes);
        return bytes;
    }
}
