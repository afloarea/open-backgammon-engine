package com.github.afloarea.obge.moves.generator;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.layout.ColumnSequence;

/**
 * A calculator that checks that a piece can be moved to a column on the board (excludes collect-type moves).
 */
public final class BasicMoveCalculator extends AbstractMoveCalculator {

    public BasicMoveCalculator(ColumnSequence columnSequence) {
        super(columnSequence);
    }

    @Override
    protected boolean canPerformMove(int from, int to, Direction direction) {
        return to < Constants.COLLECT_INDEX && columnSequence.getColumn(to, direction).isClearForDirection(direction);
    }
}
