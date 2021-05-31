package com.github.afloarea.obge.moves.executor;

import com.github.afloarea.obge.dice.DiceValues;
import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Default implementation of the {@link MoveExecutor} that splits a complex move
 * into simple moves and executes each move.
 */
public final class DefaultMoveExecutor implements MoveExecutor {
    private final ColumnSequence columns;

    public DefaultMoveExecutor(ColumnSequence columns) {
        this.columns = columns;
    }

    @Override
    public List<ObgMove> executeMove(ObgMove move, Direction direction) {
        return split(move, direction)
                .flatMap(gameMove -> performBasicMove(gameMove, direction))
                .collect(Collectors.toList());
    }

    private Stream<ObgMove> split(ObgMove move, Direction direction) {
        final var splitMoves = new ArrayList<ObgMove>();
        int fromIndex = columns.getColumnIndex(move.getSource(), direction);

        for (int distance : move.getDiceValues()) {
            final int newIndex = Math.min(fromIndex + distance, Constants.COLLECT_INDEX);
            splitMoves.add(ObgMove.of(
                    columns.getColumn(fromIndex, direction).getId(),
                    columns.getColumn(newIndex, direction).getId(),
                    DiceValues.of(distance)));
            fromIndex = newIndex;
        }

        return splitMoves.stream();
    }

    private Stream<ObgMove> performBasicMove(ObgMove move, Direction direction) {
        final var sourceColumn = columns.getColumnById(move.getSource());
        final var targetColumn = columns.getColumnById(move.getTarget());

        final var executedMoves = new ArrayList<ObgMove>();
        final var oppositeDirection = direction.reverse();

        if (targetColumn.getMovingDirectionOfElements() == oppositeDirection) {
            final var suspendColumn = columns.getSuspendedColumn(oppositeDirection);
            suspendColumn.addElement(oppositeDirection);
            targetColumn.removeElement();
            executedMoves.add(ObgMove.of(targetColumn.getId(), suspendColumn.getId(), DiceValues.NONE));
        }

        targetColumn.addElement(direction);
        sourceColumn.removeElement();
        executedMoves.add(move);

        return executedMoves.stream();
    }
}
