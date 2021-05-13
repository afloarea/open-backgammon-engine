package com.github.afloarea.obge.moves.generator;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.layout.ColumnSequence;

import static com.github.afloarea.obge.common.Constants.COLLECT_INDEX;

/**
 * This calculator allows for pieces to also be collected if the sum of dice values
 * is EXACTLY the amount required for collecting.
 */
public final class StrictCollectMoveCalculator extends AbstractMoveCalculator {

    public StrictCollectMoveCalculator(ColumnSequence columnSequence) {
        super(columnSequence);
    }

    @Override
    protected boolean canPerformMove(int from, int to, Direction direction) {
        if (to > COLLECT_INDEX) {
            return false;
        }
        if (to == COLLECT_INDEX) {
            return true;
        }
        return columnSequence.getColumn(to, direction).isClearForDirection(direction);
    }
}
