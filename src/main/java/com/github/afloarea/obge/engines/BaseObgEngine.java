package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ObgEngine;
import com.github.afloarea.obge.dice.DiceValues;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.moves.ObgTransition;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.afloarea.obge.common.Constants.PIECES_PER_PLAYER;

public abstract class BaseObgEngine implements ObgEngine {
    protected Direction currentDirection = Direction.NONE;
    protected final ColumnSequence columns;

    protected BaseObgEngine(ColumnSequence columns) {
        this.columns = columns;
    }

    protected final void validateDirection(Direction direction) {
        if (isGameComplete()) {
            throw new IllegalObgActionException("Unable to roll dice. Game is finished");
        }
        if (!isCurrentTurnDone()) {
            throw new IllegalObgActionException("Cannot update dice. Turn is not yet over");
        }
        if (direction == null || direction == Direction.NONE) {
            throw new IllegalObgActionException("Invalid direction provided");
        }
        if (direction != currentDirection.reverse() && currentDirection != Direction.NONE) {
            throw new IllegalObgActionException("Invalid direction provided.");
        }
    }

    protected final void checkTransitionPossible(Direction direction) {
        if (isGameComplete()) {
            throw new IllegalObgActionException("Game is complete. No more moves allowed");
        }

        if (direction != currentDirection || direction == Direction.NONE) {
            throw new IllegalObgActionException("Incorrect direction provided");
        }
    }

    @Override
    public final Direction getCurrentTurnDirection() {
        return currentDirection;
    }

    @Override
    public Direction getWinningDirection() {
        return Stream.of(Direction.CLOCKWISE, Direction.ANTICLOCKWISE)
                .filter(direction -> columns.getCollectColumn(direction).getPieceCount() == PIECES_PER_PLAYER)
                .findAny()
                .orElse(Direction.NONE);
    }

    @Override
    public void reset() {
        currentDirection = Direction.NONE;
        columns.reset();
    }

    // helper methods

    protected final boolean sequenceHasSourceAndTarget(List<ObgTransition> sequence, String source, String target) {
        if (!sequence.get(0).source().equals(source)) return false;
        if (sequence.get(0).target().equals(target)) return true;

        for (int index = 1; index < sequence.size(); index++) {
            if (!sequence.get(index).source().equals(sequence.get(index - 1).target())) {
                return false;
            }
            if (sequence.get(index).target().equals(target)) return true;
        }

        return false;
    }

    protected final List<ObgTransition> extractUpToTarget(List<ObgTransition> sequence, String target) {
        final int targetIndex = IntStream.range(0, sequence.size())
                .filter(index -> sequence.get(index).target().equals(target))
                .findFirst()
                .orElseThrow();

        return sequence.subList(0, targetIndex + 1);
    }

    protected final void executeSequence(List<ObgTransition> sequence) {
        sequence.forEach(transition -> {
            if (transition.isSuspending()) {
                columns.getColumnById(transition.target()).removeElement();
                columns.getColumnById(transition.suspended()).addElement(currentDirection.reverse());
            }
            columns.getColumnById(transition.source()).removeElement();
            columns.getColumnById(transition.target()).addElement(currentDirection);
        });
    }

    protected final void sequenceToMultipleMoves(List<ObgTransition> sequence, Consumer<ObgMove> moveConsumer) {
        final var first = sequence.get(0);
        moveConsumer.accept(ObgMove.of(first.source(), first.target(), DiceValues.of(first.usedDie())));

        var previous = first;
        final var dice = new ArrayList<Integer>();
        dice.add(first.usedDie());
        for (int index = 1; index < sequence.size(); index++) {
            final var next = sequence.get(index);
            if (!previous.target().equals(next.source())) {
                break;
            }
            dice.add(next.usedDie());
            moveConsumer.accept(ObgMove.of(first.source(), next.target(), DiceValues.of(dice)));
            previous = next;
        }
    }
}
