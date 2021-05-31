package com.github.afloarea.obge.moves.generator;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.moves.ObgMove;

import java.util.List;
import java.util.stream.Stream;

/**
 * A service that computes the possible moves that can be made by a player (which is associated to a direction).
 */
public interface PossibleMovesProvider {

    /**
     * Compute the possible moves that can be made in the provided direction.
     * @param dice the available dice with which to perform moves (should not be empty)
     * @param direction the direction
     * @return a stream of the possible moves
     */
    Stream<ObgMove> streamPossibleMoves(List<Integer> dice, Direction direction);

}
