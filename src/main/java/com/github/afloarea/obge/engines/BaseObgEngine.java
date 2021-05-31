package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ObgEngine;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.ColumnSequence;

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
}
