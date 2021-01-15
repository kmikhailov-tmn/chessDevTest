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
        ChessDevTest test = ChessDevTestImpl.createDefault();
        int maxIndex = test.load();

        if (maxIndex > 1) readAllInParallel(test, maxIndex);

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

    private void readAllInParallel(ChessDevTest test, int maxIndex) throws ExecutionException, InterruptedException {
        List<List<Item>> testList = readAllItemList(test, maxIndex);
        parallelGetTest(test, testList);
    }

    private List<List<Item>> readAllItemList(ChessDevTest test, int maxIndex) {
        List<List<Item>> testList = new ArrayList<>();
        List<Item> list = new ArrayList<>();
        for (int i = 1; i < maxIndex; i++) {
            byte[] bytes = test.get(i);
            list.add(new Item(i, bytes));
        }
        testList.add(list);
        return testList;
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

    private void parallelGetTest(ChessDevTest test, List<List<Item>> listOfItemList) throws InterruptedException, ExecutionException {
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

    private List<ScheduledFuture<List<Item>>> parallelSaveGetTest(ChessDevTest test) throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(16);
        List<ScheduledFuture<List<Item>>> taskList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            ScheduledFuture<List<Item>> task = executor.schedule(() -> runLongSaveGet(test), 2, TimeUnit.SECONDS);
            taskList.add(task);
        }
        executor.shutdown();
        executor.awaitTermination(2, TimeUnit.MINUTES);
        return taskList;
    }

    private List<Item> runLongSaveGet(ChessDevTest test) {
        List<Item> list = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            byte[] buffer = randomBuffer();
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

    private byte[] randomBuffer() {
        byte[] bytes = new byte[Math.abs(random.nextInt(10000)+1)];
        random.nextBytes(bytes);
        return bytes;
    }
}
