package ru.navilab.chessdevtest.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

public class FileSaver {
    public static final String INDEX_FILE_EXT = ".index";
    public static final String DATA_FILE_EXT = ".data";
    public static final String FILE_NAME_PREFIX = "storage_";
    private final SeekableByteChannel indexByteChannel;
    private final SeekableByteChannel dataByteChannel;
    private ByteBuffer intBuffer = ByteBuffer.allocate(4);
    private ByteBuffer longBuffer = ByteBuffer.allocate(8);
    private IndexAndPositionList indexAndPositionList = new IndexAndPositionList();
    private int maxIndex;

    public FileSaver(int fileIndex) {
        try {
            indexByteChannel = createChannel(fileIndex, INDEX_FILE_EXT);
            dataByteChannel = createChannel(fileIndex, DATA_FILE_EXT);
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    public static List<String> getIndexFileList(File directory) {
        return Arrays.asList(directory.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return (name.startsWith(FILE_NAME_PREFIX) && name.endsWith(INDEX_FILE_EXT));
            }
        }));
    }

    private SeekableByteChannel createChannel(int fileIndex, String indexFileExt) throws IOException {
        File file = new File(getFileName(fileIndex, indexFileExt));
        return Files.newByteChannel(file.toPath(), StandardOpenOption.READ, StandardOpenOption.WRITE, StandardOpenOption.CREATE);
    }

    private static final String getFileName(int fileIndex, String fileExt) {
        return FILE_NAME_PREFIX + fileIndex + fileExt;
    }

    public synchronized void save(int index, byte[] buffer) {
        long dataPosition = saveData(buffer);
        saveIndex(index, dataPosition);
        indexAndPositionList.add(index, dataPosition);
    }

    private void saveIndex(int index, long position) {
        try {
            seekToAppend(indexByteChannel);
            updateMaxIndex(index);
            intBuffer.rewind();
            intBuffer.putInt(index);
            indexByteChannel.write(intBuffer);
            longBuffer.rewind();
            longBuffer.putLong(position);
            indexByteChannel.write(longBuffer);
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private long saveData(byte[] buffer) {
        try {
            ByteBuffer wrappedBytes = ByteBuffer.wrap(buffer);
            seekToAppend(dataByteChannel);
            long position = dataByteChannel.position();
            intBuffer.rewind();
            intBuffer.putInt(buffer.length);
            dataByteChannel.write(intBuffer);
            dataByteChannel.write(wrappedBytes);
            return position;
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private void seekToAppend(SeekableByteChannel dataByteChannel) throws IOException {
        long size = dataByteChannel.size();
        if (size > 0) dataByteChannel.position(size - 1);
    }

    public byte[] get(int index) {
        try {
            Long dataPosition = indexAndPositionList.getDataPosition(index);
            dataByteChannel.position(dataPosition);
            dataByteChannel.read(intBuffer);
            int size = intBuffer.getInt();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            dataByteChannel.read(byteBuffer);
            return byteBuffer.array();
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    public void close() {
        try {
            indexByteChannel.close();
            dataByteChannel.close();
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    public void load() {
        try {
            indexByteChannel.position(0);
            long size = indexByteChannel.size();
            long idxPos = 0;
            while (idxPos < size) {
                indexByteChannel.read(intBuffer);
                int index = intBuffer.getInt();
                indexByteChannel.read(longBuffer);
                long dataPosition = longBuffer.getLong();
                indexAndPositionList.add(index, dataPosition);
                updateMaxIndex(index);
                idxPos = indexByteChannel.position();
            }
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private void updateMaxIndex(int index) {
        if (index > maxIndex) maxIndex = index;
    }

    public int getMaxIndex() {
        return maxIndex;
    }
}
