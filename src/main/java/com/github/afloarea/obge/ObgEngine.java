package com.github.afloarea.obge;

import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.impl.BoardEngineFactory;

import java.util.List;
import java.util.Set;

/**
 * The Open Backgammon Engine.
 */
public interface ObgEngine {

    /**
     * Apply a dice roll result.
     * If this is the first turn, either direction can apply the dice roll.
     *
     * @param direction the direction (should be either clockwise or anticlockwise depending on whose turn is it)
     * @param dice      the dice roll result
     * @throws IllegalObgActionException if the roll cannot be applied
     */
    void applyDiceRoll(Direction direction, DiceRoll dice);

    /**
     * Execute the provided move in the given direction.
     *
     * @param direction the direction in which to execute move
     * @param move      the move to execute
     * @return the list of simple moves executed
     * (each simple move corresponds to a single die value or suspend-type move)
     * @throws IllegalObgActionException if the move cannot be executed
     */
    List<ObgMove> execute(Direction direction, ObgMove move);

    /**
     * Execute the move from source to target in the given direction.
     * If there are multiple moves from the source to the target, then the one with the highest starting dice value
     * will be selected.
     *
     * @param direction the direction in which to execute move
     * @param source    the source of the move to execute
     * @param target    the target of the move to execute
     * @return the list of simple moves executed
     * (each simple move corresponds to a single die value or suspend-type move)
     * @throws IllegalObgActionException if the move cannot be executed
     */
    List<ObgMove> execute(Direction direction, String source, String target);

    // read-only methods

    /**
     * Obtain the set of possible moves (both simple and composite).
     *
     * @return a set of possible moves
     */
    Set<ObgMove> getPossibleMoves();

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
     * Create a new engine instance.
     * The used board layout is the following:
     * <ul>
     *     <li>
     *         <pre>["A" "B" "C" "D" "E" "F"   "G" "H" "I" "J" "K" "L"] for the top.</pre>
     *         The first six are the home area for the anticlockwise playing player
     *     </li>
     *     <li>
     *         <pre>["M" "N" "O" "P" "Q" "R"   "T" "U" "V" "W" "X" "Y"] for the bottom.</pre>
     *         The first six are the home area for the clockwise playing player
     *     </li>
     *     <li>
     *         "SB" and "SW" for the suspended pieces column for clockwise and anticlockwise respectively.
     *     </li>
     *     <li>
     *         "CB" and "CW" for the collected pieces column for clockwise and anticlockwise respectively.
     *     </li>
     * </ul>
     *
     * @return a new arranged board
     */
    static ObgEngine create() {
        return create(BoardEngineFactory.DEFAULT_BOARD_TEMPLATE);
    }

    /**
     * Create a new engine instance using the provided template.
     * <ul>
     *     <li>The template must have 3 arrays.</li>
     *     <li>The first array of length 12 represents the top columns ids from left to right.
     *     These will include the home area for the anticlockwise direction</li>
     *     <li>The second array of length 12 represents the bottom columns ids from left to right.
     *     These will include the home area for the clockwise direction</li>
     *     <li>The third array of length 4 represents ids for the suspended clockwise, suspended anticlockwise,
     *     collected clockwise, collected anticlockwise columns</li>
     *     <li>All ids in the template should be unique</li>
     * </ul>
     *
     * @return a new arranged board
     */
    static ObgEngine create(String[][] template) {
        return BoardEngineFactory.buildDefaultWithTemplate(template);
    }
}
