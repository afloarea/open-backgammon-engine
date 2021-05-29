package com.github.afloarea.obge.board;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ObgTransition;

import java.util.List;

public interface ObgBoard {

    void doSequence(List<ObgTransition> sequence, Direction direction);

    void undoSequence(List<ObgTransition> sequence, Direction direction);

    ColumnInfo getSuspendColumn(Direction direction);

    ColumnInfo getCollectColumn(Direction direction);

    List<ColumnInfo> getNormalColumns(Direction direction);

    static ObgBoard createStartingBoard() {
        return null;
    }

}
