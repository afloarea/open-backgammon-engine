package com.github.afloarea.obge;

import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.moves.ObgTransition;

import java.util.List;
import java.util.Set;

/**
 * Engine that works with partial, simple moves.
 */
public interface InteractiveObgEngine extends ObgEngine {

    /**
     * Execute the move from source to target in the given direction.
     * If there are multiple moves from the source to the target, then the DiceRoll order is used.
     *
     * @param direction the direction in which to execute move
     * @param source    the source of the move to execute
     * @param target    the target of the move to execute
     * @return the list of simple moves executed
     * (each simple move corresponds to a single die value or suspend-type move)
     * @throws IllegalObgActionException if the move cannot be executed
     */
    List<ObgTransition> execute(Direction direction, String source, String target);

    /**
     * Obtain the set of possible moves (both simple and composite).
     *
     * @return a set of possible moves
     */
    Set<ObgMove> getPossibleMoves();
}
