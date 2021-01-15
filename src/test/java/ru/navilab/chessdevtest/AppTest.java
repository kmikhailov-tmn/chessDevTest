package ru.navilab.chessdevtest;

import org.junit.Assert;
import org.junit.Test;
import ru.navilab.chessdevtest.impl.ChessDevTestImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class AppTest {
    private final Random random = new Random();

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
    public void bigTest() throws InterruptedException, ExecutionException {
        ChessDevTestImpl test = ChessDevTestImpl.createDefault();
        test.init();

        // test save then get in parallel
        List<ScheduledFuture<List<Item>>> taskList = parallelSaveGetTest(test);

        List<List<Item>> listOfItemList = getListsOfItemList(taskList);

        // test read in parallel
        parallelGetTest(test, listOfItemList);

        // and once again without cache
        test.clearCache();
        parallelGetTest(test, listOfItemList);
        test.close();
    }

    private List<List<Item>> getListsOfItemList(List<ScheduledFuture<List<Item>>> taskList) throws InterruptedException, ExecutionException {
        List<List<Item>> listOfItemList = new ArrayList<>();
        for (ScheduledFuture<List<Item>> future : taskList) {
            listOfItemList.add(future.get());
        }
        return listOfItemList;
    }

    private final List<Item> getItems(ScheduledFuture<List<Item>> task) {
        try {
            return task.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private void parallelGetTest(ChessDevTestImpl test, List<List<Item>> listOfItemList) throws InterruptedException, ExecutionException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);
        for (List<Item> itemList : listOfItemList) {
            executor.schedule(()-> {
                itemList.forEach((item) -> {
                    byte[] bytes = test.get(item.index);
                    Assert.assertArrayEquals(item.bytes, bytes);
                });
            }, 0, TimeUnit.SECONDS);
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
    }

    private List<ScheduledFuture<List<Item>>> parallelSaveGetTest(ChessDevTestImpl test) throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);
        List<ScheduledFuture<List<Item>>> taskList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            ScheduledFuture<List<Item>> task = executor.schedule(() -> runLongSaveGet(test), 2, TimeUnit.SECONDS);
            taskList.add(task);
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
        return taskList;
    }

    private List<Item> runLongSaveGet(ChessDevTestImpl test) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            byte[] buffer = randomBuffer(10000);
            int index = test.save(buffer);
            list.add(new Item(index, buffer));
        }
        if (random.nextInt(100) > 70) test.clearCache();
        Collections.shuffle(list);
        list.parallelStream().forEach((item) -> {
            byte[] bytes = test.get(item.index);
            Assert.assertArrayEquals(item.bytes, bytes);
        });
        return list;
    }

    private byte[] randomBuffer(int bound) {
        byte[] bytes = new byte[random.nextInt(bound)];
        random.nextBytes(bytes);
        return bytes;
    }
}
