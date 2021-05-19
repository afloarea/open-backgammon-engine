package com.github.afloarea.obge.moves.executor;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ObgMove;

import java.util.List;

/**
 * Interface for executing game moves.
 */
public interface MoveExecutor {

    /**
     * Execute the provided move with the specified direction.
     * @param move the move to execute
     * @param direction the direction of the move
     * @return a list of performed simple moves (ex. suspend piece + move piece)
     */
    List<ObgMove> executeMove(ObgMove move, Direction direction);

}
