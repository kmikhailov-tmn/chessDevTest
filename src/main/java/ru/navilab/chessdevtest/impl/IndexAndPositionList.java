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

    /**
     * @param index
     * @return dataPosition or null if not found
     */
    public Long getDataPosition(int index) {
        int indexOfIndex = indexList.indexOf(index);
        if (indexOfIndex == -1) return null;
        return dataPositionList.get(indexOfIndex);
    }
}
