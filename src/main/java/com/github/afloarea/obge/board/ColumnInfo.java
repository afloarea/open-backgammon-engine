package com.github.afloarea.obge.board;

import com.github.afloarea.obge.Direction;

public interface ColumnInfo {

    String getId();

    int getPieceCount();

    Direction getMovingDirectionOfElements();

}
