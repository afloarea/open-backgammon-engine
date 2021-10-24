package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.TurnBasedObgEngine;
import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.layout.BoardMapper;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.predictors.BoardPredictor;
import com.github.afloarea.obge.predictors.ObgPredictor;

import java.util.HashSet;
import java.util.Set;

public final class BoardStatePredictingObgEngine extends BaseObgEngine implements TurnBasedObgEngine {

    private final Set<BoardSnapshot> possibleBoards = new HashSet<>();
    private final ObgPredictor<Set<BoardSnapshot>> boardPredictor = new BoardPredictor();

    public BoardStatePredictingObgEngine(ColumnSequence columns) {
        super(columns);
    }

    @Override
    public void applyDiceRoll(Direction direction, DiceRoll dice) {
        validateDirection(direction);

        currentDirection = direction;
        possibleBoards.clear();
        possibleBoards.addAll(boardPredictor.predict(columns, dice, direction));
    }

    @Override
    public boolean isCurrentTurnDone() {
        return possibleBoards.isEmpty();
    }

    @Override
    public Set<BoardSnapshot> getBoardChoices() {
        return Set.copyOf(possibleBoards);
    }

    @Override
    public BoardSnapshot chooseBoard(Direction playingDirection, BoardSnapshot board) {
        checkTransitionPossible(playingDirection);
        BoardMapper.loadSnapshot(columns, board);
        possibleBoards.clear();
        return board;
    }
}
