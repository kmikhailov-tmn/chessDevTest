package ru.navilab.chessdevtest.impl;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class FileSaver {
    public static final String INDEX_FILE_EXT = ".index";
    public static final String DATA_FILE_EXT = ".data";
    public static final String FILE_NAME_PREFIX = "storage_";
    private final SeekableByteChannel indexByteChannel;
    private final SeekableByteChannel dataByteChannel;
    private ByteBuffer intBuffer = ByteBuffer.allocate(4);
    private ByteBuffer longBuffer = ByteBuffer.allocate(8);

    public FileSaver(int fileIndex) {
        try {
            indexByteChannel = createChannel(fileIndex, INDEX_FILE_EXT);
            dataByteChannel = createChannel(fileIndex, DATA_FILE_EXT);
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private SeekableByteChannel createChannel(int fileIndex, String indexFileExt) throws IOException {
        File file = new File(getFileName(fileIndex, indexFileExt));
        return Files.newByteChannel(file.toPath(), StandardOpenOption.APPEND);
    }

    private static final String getFileName(int fileIndex, String fileExt) {
        return FILE_NAME_PREFIX + fileIndex + fileExt;
    }

    public synchronized void save(int index, byte[] buffer) {
        long position = saveData(buffer);
        saveIndex(index, position);
    }

    private void saveIndex(int index, long position) {
        try {
            intBuffer.putInt(index);
            indexByteChannel.write(intBuffer);
            longBuffer.putLong(position);
            indexByteChannel.write(longBuffer);
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    private long saveData(byte[] buffer) {
        try {
            ByteBuffer wrappedBytes = ByteBuffer.wrap(buffer);
            long position = dataByteChannel.position();
            intBuffer.putInt(buffer.length);
            dataByteChannel.write(intBuffer);
            dataByteChannel.write(wrappedBytes);
            return position;
        } catch (IOException e) {
            throw new PersistLayerException(e);
        }
    }

    public byte[] get(int index) {
        return new byte[0];
    }
}
