package com.github.afloarea.obge;

import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;

import java.util.Set;

/**
 * Engine optimized for working with board states.
 */
public interface TurnBasedObgEngine extends ObgEngine {

    /**
     * Get the possible board states after rolling dice for the current playing direction.
     *
     * @return the board states
     */
    Set<BoardSnapshot> getBoardChoices();

    /**
     * Choose a board state, advancing the game.
     *
     * @param playingDirection the playing direction
     * @param board            the chosen board
     * @return the selected board
     * @throws IllegalObgActionException if an invalid board is provided
     */
    BoardSnapshot chooseBoard(Direction playingDirection, BoardSnapshot board);

}
