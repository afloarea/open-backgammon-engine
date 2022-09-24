package com.github.afloarea.obge;

import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.moves.ObgTransition;

import java.util.List;

/**
 * Hybrid Engine that provides the capabilities of both {@link InteractiveObgEngine} and {@link TurnBasedObgEngine}.
 * Has a larger memory footprint that either of them.
 */
public interface MixedModeObgEngine extends InteractiveObgEngine, TurnBasedObgEngine {

    /**
     * Choose a board to transition to.
     *
     * @param playingDirection the playing direction
     * @param boardSnapshot    the board
     * @return the moves executed to transition to the provided board
     */
    List<ObgTransition> transitionTo(Direction playingDirection, BoardSnapshot boardSnapshot);

}
