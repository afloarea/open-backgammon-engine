package com.github.afloarea.obge;

import com.github.afloarea.obge.exceptions.IllegalBgActionException;
import com.github.afloarea.obge.impl.BoardFactory;

import java.util.List;
import java.util.Set;

/**
 * Backgammon board representation.
 * The board layout is the following:
 * <ul>
 *     <li>
 *         <pre>["A" "B" "C" "D" "E" "F"   "G" "H" "I" "J" "K" "L"] for the top.</pre>
 *     </li>
 *     <li>
 *         <pre>["M" "N" "O" "P" "Q" "R"   "T" "U" "V" "W" "X" "Y"] for the bottom.</pre>
 *     </li>
 *     <li>
 *         "SB" and "SW" for the suspended pieces column for clockwise and anticlockwise respectively.
 *     </li>
 *     <li>
 *         "CB" and "CW" for the collected pieces column for clockwise and anticlockwise respectively.
 *     </li>
 * </ul>
 */
public interface BgBoard {

    /**
     * Apply a dice roll result.
     * If this is the first turn, either direction can apply the dice roll.
     *
     * @param direction the direction (should be either clockwise or anticlockwise depending on whose turn is it)
     * @param dice      the dice roll result
     * @throws IllegalBgActionException if the roll cannot be applied
     */
    void applyDiceRoll(Direction direction, DiceRoll dice);

    /**
     * Execute the provided move in the given direction.
     *
     * @param direction the direction in which to execute move
     * @param move      the move to execute
     * @return the list of simple moves executed (each simple move corresponds to a single die value)
     * @throws IllegalBgActionException if the move cannot be executed
     */
    List<BgMove> execute(Direction direction, BgMove move);

    // read-only methods

    /**
     * Obtain the set of possible moves (both simple and composite).
     *
     * @return a set of possible moves
     */
    Set<BgMove> getPossibleMoves();

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
    boolean isGameComplete();

    /**
     * Check if the current direction has finished the turn.
     *
     * @return true if the current direction has no moves left to make.
     */
    boolean isCurrentTurnDone();

    /**
     * Create a new board.
     *
     * @return a new arranged board
     */
    static BgBoard build() {
        return BoardFactory.buildDefaultBoard();
    }
}
