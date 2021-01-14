package ru.navilab.chessdevtest.impl;

import java.util.ArrayList;
import java.util.List;

public class IndexAndPositionList {
    private List<Integer> indexList = new ArrayList<>();
    private List<Long> dataPositionList = new ArrayList<>();

    public void add(int index, long dataPosition) {
        indexList.add(index);
        dataPositionList.add(dataPosition);
    }

    public Long getDataPosition(int index) {
        int indexOfIndex = indexList.indexOf(index);
        return dataPositionList.get(indexOfIndex);
    }
}
