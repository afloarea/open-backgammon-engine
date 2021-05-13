package com.github.afloarea.obge.moves.generator;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.common.Move;

import java.util.List;
import java.util.stream.Stream;

/**
 * A contract for calculating possible moves for a given start column index.
 */
public interface MoveCalculator {

    /**
     * Compute the available moves for a given start column index.
     * @param startIndex the start index
     * @param availableHops the available dice/distance elements
     * @param direction the direction
     * @return the possible moves
     */
    Stream<Move> computeMovesFromStart(int startIndex, List<Integer> availableHops, Direction direction);

}
