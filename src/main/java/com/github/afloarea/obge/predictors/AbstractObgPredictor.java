package com.github.afloarea.obge.predictors;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.layout.BoardColumn;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgTransition;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public abstract class AbstractObgPredictor<R, A> implements ObgPredictor<R> {

    private Direction currentDirection = Direction.NONE;
    protected ColumnSequence columns;

    protected final A aggregator;

    private final Deque<Integer> availableDice = new ArrayDeque<>();
    protected final Deque<ObgTransition> performedMoves = new ArrayDeque<>();

    protected AbstractObgPredictor(A aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public R predict(ColumnSequence columns, DiceRoll diceRoll, Direction direction) {
        this.columns = columns;
        this.currentDirection = direction;
        clearAggregator();

        if (diceRoll.isSimple()) {
            predict(List.of(diceRoll.dice1(), diceRoll.dice2()));
            predict(List.of(diceRoll.dice2(), diceRoll.dice1()));
        } else {
            predict(diceRoll.stream().boxed().toList());
        }

        return mapAggregatorToResult();
    }

    protected abstract void clearAggregator();

    protected abstract R mapAggregatorToResult();

    protected abstract void save();

    private void predict(List<Integer> availableDiceValues) {
        performedMoves.clear();
        availableDice.clear();
        availableDice.addAll(availableDiceValues);
        computeSequences();
    }

    private void computeSequences() {
        if (availableDice.isEmpty()) {
            save();
            return;
        }

        final var dieValue = availableDice.getFirst();
        // handle suspend column
        final var suspendColumn = columns.getSuspendedColumn(currentDirection);
        if (!suspendColumn.isEmpty()) {
            if (!canMove(suspendColumn, dieValue)) {
                save();
                return;
            }
            final var executedMove = doMove(suspendColumn, dieValue);
            computeSequences();
            undoMove(executedMove);
            return;
        }

        // handle normal columns
        boolean moved = false;
        final var normalColumns = columns.stream(currentDirection).skip(1).toList();
        for (var column : normalColumns) {
            if (canMove(column, dieValue)) {
                final var executedMove = doMove(column, dieValue);
                computeSequences();
                undoMove(executedMove);
                moved = true;
            }
        }

        if (!moved) {
            save();
        }
    }

    private boolean canMove(BoardColumn source, int distance) {
        final var sourceIndex = columns.getColumnIndex(source, currentDirection);
        if (source.getMovingDirectionOfElements() != currentDirection) {
            return false;
        }

        final var targetIndex = sourceIndex + distance;

        if (sourceIndex < Constants.HOME_START) {
            final var target = columns.getColumn(targetIndex, currentDirection);
            return target.isClearForDirection(currentDirection);
        }

        var nonHomePieces = columns.countPiecesUpToIndex(Constants.HOME_START, currentDirection);
        if (nonHomePieces > 0) {
            if (targetIndex >= Constants.COLLECT_INDEX) {
                return false;
            }
            return columns.getColumn(targetIndex, currentDirection).isClearForDirection(currentDirection);
        }

        var piecesBehind = columns.countPiecesUpToIndex(sourceIndex, currentDirection);
        if (piecesBehind == 0) {
            return targetIndex >= Constants.COLLECT_INDEX
                    || columns.getColumn(targetIndex, currentDirection).isClearForDirection(currentDirection);
        }

        if (targetIndex > Constants.COLLECT_INDEX) {
            return false;
        }

        return columns.getColumn(targetIndex, currentDirection).isClearForDirection(currentDirection);
    }

    private ObgTransition doMove(BoardColumn source, int distance) {
        availableDice.removeFirst();

        final var sourceIndex = columns.getColumnIndex(source, currentDirection);
        final var targetIndex = Math.min(sourceIndex + distance, Constants.COLLECT_INDEX);
        final var target = columns.getColumn(targetIndex, currentDirection);

        final var opponentDirection = currentDirection.reverse();
        String suspended = null;
        if (target.getMovingDirectionOfElements() == opponentDirection) {
            final var suspendedColumn = columns.getSuspendedColumn(opponentDirection);
            suspendedColumn.addElement(opponentDirection);
            target.removeElement();
            suspended = suspendedColumn.getId();
        }
        target.addElement(currentDirection);
        source.removeElement();

        final var move = new ObgTransition(source.getId(), target.getId(), distance, suspended);
        performedMoves.addLast(move);
        return move;
    }

    private void undoMove(ObgTransition move) {
        availableDice.addFirst(move.usedDie());

        final var originalSource = columns.getColumnById(move.source());
        final var originalTarget = columns.getColumnById(move.target());

        originalTarget.removeElement();
        originalSource.addElement(currentDirection);

        if (move.isSuspending()) {
            final var opponentDirection = currentDirection.reverse();
            columns.getSuspendedColumn(opponentDirection).removeElement();
            originalTarget.addElement(opponentDirection);
        }

        performedMoves.removeLast();
    }

}
