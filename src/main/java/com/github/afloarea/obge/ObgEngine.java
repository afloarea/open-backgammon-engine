package com.github.afloarea.obge;

import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;

/**
 * The Open Backgammon Engine.
 */
public interface ObgEngine {

    /**
     * Apply a die roll result.
     * If this is the first turn, either direction can apply the dice roll.
     *
     * @param direction the direction (should be either clockwise or anticlockwise depending on whose turn is it)
     * @param dice      the dice roll result
     * @throws IllegalObgActionException if the roll cannot be applied
     */
    void applyDiceRoll(Direction direction, DiceRoll dice);

    /**
     * Get the current playing direction.
     *
     * @return the direction
     */
    Direction getCurrentTurnDirection();

    /**
     * If the game is complete get the winning direction.
     *
     * @return the winning direction
     */
    Direction getWinningDirection();

    /**
     * If the game is complete get the losing direction.
     *
     * @return the losing direction
     */
    default Direction getLosingDirection() {
        return getWinningDirection().reverse();
    }

    /**
     * Check if the game is finished.
     *
     * @return whether the game is complete or not.
     */
    default boolean isGameComplete() {
        return getWinningDirection() != Direction.NONE;
    }

    /**
     * Check if the current direction has finished the turn.
     *
     * @return true if the current direction has no moves left to make.
     */
    boolean isCurrentTurnDone();

    /**
     * Reset the engine to the starting state.
     */
    void reset();
}
