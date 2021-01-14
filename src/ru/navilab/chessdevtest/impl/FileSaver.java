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
            writeInt(index, indexByteChannel);
            writeLong(position);
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private void writeLong(long position) throws IOException {
        longBuffer.rewind();
        longBuffer.putLong(position);
        longBuffer.rewind();
        indexByteChannel.write(longBuffer);
    }

    private void writeInt(int index, SeekableByteChannel indexByteChannel) throws IOException {
        intBuffer.rewind();
        intBuffer.putInt(index);
        intBuffer.rewind();
        indexByteChannel.write(intBuffer);
    }

    private long saveData(byte[] buffer) {
        try {
            ByteBuffer wrappedBytes = ByteBuffer.wrap(buffer);
            seekToAppend(dataByteChannel);
            long position = dataByteChannel.position();
            writeInt(buffer.length, dataByteChannel);
            dataByteChannel.write(wrappedBytes);
            return position;
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private void seekToAppend(SeekableByteChannel byteChannel) throws IOException {
        long size = byteChannel.size();
        byteChannel.position(size);
    }

    public byte[] get(int index) {
        try {
            Long dataPosition = indexAndPositionList.getDataPosition(index);
            dataByteChannel.position(dataPosition);
            int size = getInt(dataByteChannel);
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            dataByteChannel.read(byteBuffer);
            return byteBuffer.array();
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private int getInt(SeekableByteChannel dataByteChannel) throws IOException {
        intBuffer.rewind();
        dataByteChannel.read(intBuffer);
        intBuffer.rewind();
        return intBuffer.getInt();
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
                int index = getInt(indexByteChannel);
                long dataPosition = getLong(indexByteChannel);
                indexAndPositionList.add(index, dataPosition);
                updateMaxIndex(index);
                idxPos = indexByteChannel.position();
            }
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private long getLong(SeekableByteChannel indexByteChannel) throws IOException {
        longBuffer.rewind();
        indexByteChannel.read(longBuffer);
        longBuffer.rewind();
        long dataPosition = longBuffer.getLong();
        return dataPosition;
    }

    private void updateMaxIndex(int index) {
        if (index > maxIndex) maxIndex = index;
    }

    public int getMaxIndex() {
        return maxIndex;
    }
}
