package com.github.afloarea.obge.board;

import com.github.afloarea.obge.Direction;

/**
 * A snapshot of the board column containing the number of pieces for that column and
 * the direction that the pieces should be moving.
 */
public record ColumnSnapshot(int pieceCount, Direction elementsDirection) {
}
