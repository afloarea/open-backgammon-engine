package com.github.afloarea.obge.moves.generator;

import com.github.afloarea.obge.dice.DiceValues;
import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * Base class for computing possible moves for a column.
 */
public abstract class AbstractMoveCalculator implements MoveCalculator {

    protected final ColumnSequence columnSequence;

    protected AbstractMoveCalculator(ColumnSequence columnSequence) {
        this.columnSequence = columnSequence;
    }

    @Override
    public Stream<ObgMove> computeMovesFromStart(int startIndex, List<Integer> availableHops, Direction direction) {
        if (availableHops.isEmpty()) {
            return Stream.empty();
        }
        final var usedHops = new ArrayList<Integer>();
        final var moves = new ArrayList<ObgMove>();

        int index = startIndex;
        for (int hop : availableHops) {
            final int newIndex = index + hop;
            if (!canPerformMove(index, newIndex, direction)) {
                return moves.stream();
            }

            usedHops.add(hop);
            moves.add(ObgMove.of(
                    columnSequence.getColumn(startIndex, direction).getId(),
                    columnSequence.getColumn(Math.min(Constants.COLLECT_INDEX, newIndex), direction).getId(),
                    DiceValues.of(usedHops)));
            index = newIndex;
        }

        return moves.stream();
    }

    /**
     * Check whether a piece can be moved to a column.
     * @param from the source column
     * @param to the target column
     * @param direction the direction
     * @return whether or not a piece can be moved
     */
    protected abstract boolean canPerformMove(int from, int to, Direction direction);
}
