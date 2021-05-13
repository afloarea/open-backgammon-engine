package com.github.afloarea.obge.moves.generator;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.common.Move;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.List;
import java.util.stream.Stream;

/**
 * This calculator is similar to {@link BasicMoveCalculator} but it also allows a piece
 * to be collected if the sum of the dice values is greater that the number of columns.
 *
 * This is typically used for the farthest piece which may be collected if the roll is high enough
 * and there is at most one piece outside of the home area.
 */
public final class PermissiveCollectMoveCalculator extends AbstractMoveCalculator {

    private boolean checkedOnce = false;

    public PermissiveCollectMoveCalculator(ColumnSequence columnSequence) {
        super(columnSequence);
    }

    @Override
    public final Stream<Move> computeMovesFromStart(int startIndex, List<Integer> availableHops, Direction direction) {
        checkedOnce = false;
        return super.computeMovesFromStart(startIndex, availableHops, direction);
    }

    @Override
    protected boolean canPerformMove(int from, int to, Direction direction) {
        if (to < Constants.COLLECT_INDEX) {
            return columnSequence.getColumn(to, direction).isClearForDirection(direction);
        }
        if (checkedOnce) {
            return false;
        }
        checkedOnce = true;
        return to == Constants.COLLECT_INDEX || columnSequence.countPiecesUpToIndex(from, direction) <= 1;
    }
}
