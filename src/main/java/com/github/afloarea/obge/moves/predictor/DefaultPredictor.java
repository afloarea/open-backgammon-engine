package com.github.afloarea.obge.moves.predictor;

import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.layout.BoardColumn;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.*;
import java.util.stream.Collectors;

public final class DefaultPredictor implements ObgPredictor {

    private Direction currentDirection = Direction.NONE;
    private ColumnSequence columns;

    private final Set<List<ObgTransition>> sequences = new HashSet<>();

    private final Deque<Integer> availableDice = new ArrayDeque<>();
    private final Deque<ObgTransition> performedMoves = new ArrayDeque<>();

    @Override
    public Set<List<ObgTransition>> predict(ColumnSequence columns, DiceRoll diceRoll, Direction direction) {
        this.currentDirection = direction;
        this.columns = columns;
        sequences.clear();

        if (diceRoll.isSimple()) {
            predict(List.of(diceRoll.getDice1(), diceRoll.getDice2()));
            predict(List.of(diceRoll.getDice2(), diceRoll.getDice1()));
        } else {
            predict(diceRoll.stream().boxed().collect(Collectors.toList()));
        }

        final var maxMoves = sequences.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        sequences.removeIf(sequence -> sequence.size() < maxMoves);

        return Set.copyOf(sequences);
    }

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

        final var dieValue = availableDice.element();
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
        final var normalColumns = columns.stream(currentDirection).skip(1).collect(Collectors.toList());
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

        if (sourceIndex < Constants.HOME_START) {
            final var target = columns.getColumn(sourceIndex + distance, currentDirection);
            return target.isClearForDirection(currentDirection);
        }

        var nonHomePieces = columns.countPiecesUpToIndex(Constants.HOME_START, currentDirection);
        if (nonHomePieces > 0) {
            var targetIndex = sourceIndex + distance;
            if (targetIndex >= Constants.COLLECT_INDEX) {
                return false;
            }
            return columns.getColumn(targetIndex, currentDirection).isClearForDirection(currentDirection);
        }

        var piecesBehind = columns.countPiecesUpToIndex(sourceIndex, currentDirection);
        var targetIndex = sourceIndex + distance;
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
        var suspended = false;
        if (target.getMovingDirectionOfElements() == opponentDirection) {
            columns.getSuspendedColumn(opponentDirection).addElement(opponentDirection);
            target.removeElement();
            suspended = true;
        }
        target.addElement(currentDirection);
        source.removeElement();

        final var move = new ObgTransition(source.getId(), target.getId(), distance, suspended);
        performedMoves.addLast(move);
        return move;
    }

    private void undoMove(ObgTransition move) {
        availableDice.addFirst(move.getUsedDie());

        final var originalSource = columns.getColumnById(move.getSource());
        final var originalTarget = columns.getColumnById(move.getTarget());

        originalTarget.removeElement();
        originalSource.addElement(currentDirection);

        if (move.isSuspending()) {
            final var opponentDirection = currentDirection.reverse();
            columns.getSuspendedColumn(opponentDirection).removeElement();
            originalTarget.addElement(opponentDirection);
        }

        performedMoves.removeLast();
    }

    private void save() {
        if (performedMoves.isEmpty()) {
            return;
        }
        sequences.add(List.copyOf(performedMoves));
    }

}
