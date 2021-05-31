package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.*;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.moves.executor.DefaultMoveExecutor;
import com.github.afloarea.obge.moves.executor.MoveExecutor;
import com.github.afloarea.obge.moves.generator.DefaultMoveProvider;
import com.github.afloarea.obge.moves.generator.PossibleMovesProvider;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.afloarea.obge.common.Constants.PIECES_PER_PLAYER;

/**
 * Engine implementation.
 */
public final class BasicObgEngine extends BaseObgEngine implements InteractiveObgEngine {

    private final Set<ObgMove> possibleMoves = new LinkedHashSet<>();

    private final List<Integer> remainingDiceValues = new ArrayList<>();

    private final PossibleMovesProvider defaultMoveProvider;
    private final MoveExecutor moveExecutor;

    public BasicObgEngine(ColumnSequence columns) {
        super(columns);
        this.defaultMoveProvider = new DefaultMoveProvider(columns);
        this.moveExecutor = new DefaultMoveExecutor(columns);
    }

    @Override
    public void applyDiceRoll(Direction direction, DiceRoll dice) {
        validateDirection(direction);

        currentDirection = direction;
        dice.stream().boxed().forEach(remainingDiceValues::add);

        updatePossibleMoves();
    }

    private void updatePossibleMoves() {
        possibleMoves.clear();
        if (remainingDiceValues.isEmpty() || isGameComplete()) {
            return;
        }
        final var computedMoves = defaultMoveProvider.streamPossibleMoves(remainingDiceValues, currentDirection)
                        .collect(Collectors.toUnmodifiableSet());

        possibleMoves.addAll(computedMoves);

        if (possibleMoves.isEmpty()) {
            remainingDiceValues.clear();
        }
    }

    @Override
    public boolean isCurrentTurnDone() {
        return remainingDiceValues.isEmpty();
    }

    @Override
    public Set<ObgMove> getPossibleMoves() {
        return Set.copyOf(possibleMoves);
    }

    @Override
    public List<ObgMove> execute(Direction direction, String source, String target) {
        checkTransitionPossible(direction);
        final var selectedMove = possibleMoves.stream()
                .filter(move -> move.getSource().equals(source) && move.getTarget().equals(target))
                .findFirst()
                .orElseThrow(() -> new IllegalObgActionException("Invalid move provided"));

        return executeMove(selectedMove);
    }

    @Override
    public List<ObgMove> execute(Direction direction, ObgMove move) {
        checkMoveIsExecutable(direction, move);
        return executeMove(move);
    }

    private void checkMoveIsExecutable(Direction direction, ObgMove move) {
        checkTransitionPossible(direction);

        if (!possibleMoves.contains(move)) {
            throw new IllegalObgActionException("Invalid move provided");
        }
    }

    private List<ObgMove> executeMove(ObgMove selectedMove) {
        selectedMove.getDiceValues().forEach(remainingDiceValues::remove);
        final var executedMoves = moveExecutor.executeMove(selectedMove, currentDirection);
        updatePossibleMoves(); // update after executing the moves
        return executedMoves;
    }

    @Override
    public boolean isGameComplete() {
        return Stream.of(Direction.CLOCKWISE, Direction.ANTICLOCKWISE)
                .map(columns::getCollectColumn)
                .anyMatch(column -> column.getPieceCount() == PIECES_PER_PLAYER);
    }

    @Override
    public Direction getWinningDirection() {
        return Stream.of(Direction.CLOCKWISE, Direction.ANTICLOCKWISE)
                .filter(direction -> columns.getCollectColumn(direction).getPieceCount() == PIECES_PER_PLAYER)
                .findAny()
                .orElse(Direction.NONE);
    }
}
