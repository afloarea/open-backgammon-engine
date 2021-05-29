package com.github.afloarea.obge.impl;

import com.github.afloarea.obge.*;
import com.github.afloarea.obge.ObgTransition;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.executor.DefaultMoveExecutor;
import com.github.afloarea.obge.moves.executor.MoveExecutor;
import com.github.afloarea.obge.moves.predictor.DefaultPredictor;
import com.github.afloarea.obge.moves.predictor.ObgPredictor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.afloarea.obge.common.Constants.PIECES_PER_PLAYER;
import static java.util.function.Predicate.not;

public final class PredictingObgEngine implements ObgEngine {

    private Direction currentDirection = Direction.NONE;
    private final ColumnSequence columns;
    private final ObgPredictor predictor;
    private final MoveExecutor moveExecutor;

    private final Set<List<ObgTransition>> possibleTransitions = new HashSet<>();

    public PredictingObgEngine(ColumnSequence columns) {
        this.columns = columns;
        this.predictor = new DefaultPredictor();
        this.moveExecutor = new DefaultMoveExecutor(columns);
    }

    @Override
    public void applyDiceRoll(Direction direction, DiceRoll dice) {
        validateDirection(direction);

        currentDirection = direction;
        possibleTransitions.addAll(predictor.predict(columns, dice, direction));
    }

    private void validateDirection(Direction direction) {
        if (isGameComplete()) {
            throw new IllegalObgActionException("Unable to roll dice. Game is finished");
        }
        if (!possibleTransitions.isEmpty()) {
            throw new IllegalObgActionException("Cannot update dice. Turn is not yet over");
        }
        if (direction == null || direction == Direction.NONE) {
            throw new IllegalObgActionException("Invalid direction provided");
        }
        if (direction != currentDirection.reverse() && currentDirection != Direction.NONE) {
            throw new IllegalObgActionException("Wrong player color rolled dice.");
        }
    }

    @Override
    public List<ObgMove> execute(Direction direction, ObgMove move) {
        checkTransitionPossible(direction);
        final var foundMove = possibleTransitions.stream()
                .flatMap(this::mapSequenceToIterativeMove)
                .filter(move::equals)
                .findFirst()
                .orElseThrow(() -> new IllegalObgActionException("Invalid move provided"));

        return executeMove(foundMove);
    }

    @Override
    public List<ObgMove> execute(Direction direction, String source, String target) {
        checkTransitionPossible(direction);
        final var foundMove = possibleTransitions.stream()
                .flatMap(this::mapSequenceToIterativeMove)
                .filter(move -> move.getSource().equals(source) && move.getTarget().equals(target))
                .findFirst()
                .orElseThrow(() -> new IllegalObgActionException("Invalid move provided"));

        return executeMove(foundMove);
    }

    private void checkTransitionPossible(Direction direction) {
        if (isGameComplete()) {
            throw new IllegalObgActionException("Game is complete. No more moves allowed");
        }

        if (direction != currentDirection || direction == Direction.NONE) {
            throw new IllegalObgActionException("Incorrect direction provided");
        }
    }

    private List<ObgMove> executeMove(ObgMove selectedMove) {
        final var executedMoves = moveExecutor.executeMove(selectedMove, currentDirection);
        filterRemainingOptions(selectedMove);
        return executedMoves;
    }

    private void filterRemainingOptions(ObgMove move) {
        final var filteredTransitions = possibleTransitions.stream()
                .filter(sequence -> sequence.get(0).getSource().equals(move.getSource()))
                .filter(sequence -> sequence.get(move.getDiceValues().size() - 1).getTarget().equals(move.getTarget()))
                .map(sequence -> sequence.stream().skip(move.getDiceValues().size()).collect(Collectors.toList()))
                .filter(not(List::isEmpty))
                .collect(Collectors.toSet());

        possibleTransitions.clear();
        possibleTransitions.addAll(filteredTransitions);
    }

    @Override
    public Set<ObgMove> getPossibleMoves() {
        return possibleTransitions.stream()
                .flatMap(this::mapSequenceToIterativeMove)
                .collect(Collectors.toSet());
    }

    private Stream<ObgMove> mapSequenceToIterativeMove(List<ObgTransition> sequence) {
        final var first = sequence.get(0);
        final var result = new ArrayList<ObgMove>();
        result.add(ObgMove.of(first.getSource(), first.getTarget(), DiceValues.of(first.getUsedDie())));

        var previous = first;
        final var dice = new ArrayList<Integer>();
        dice.add(first.getUsedDie());
        for (int index = 1; index < sequence.size(); index++) {
            final var next = sequence.get(index);
            if (!previous.getTarget().equals(next.getSource())) {
                break;
            }
            dice.add(next.getUsedDie());
            result.add(ObgMove.of(first.getSource(), next.getTarget(), DiceValues.of(dice)));
            previous = next;
        }

        return result.stream();
    }

    @Override
    public Direction getCurrentTurnDirection() {
        return currentDirection;
    }

    @Override
    public Direction getWinningDirection() {
        return Stream.of(Direction.CLOCKWISE, Direction.ANTICLOCKWISE)
                .filter(direction -> columns.getCollectColumn(direction).getPieceCount() == PIECES_PER_PLAYER)
                .findAny()
                .orElse(Direction.NONE);
    }

    @Override
    public boolean isGameComplete() {
        return Stream.of(Direction.CLOCKWISE, Direction.ANTICLOCKWISE)
                .map(columns::getCollectColumn)
                .anyMatch(column -> column.getPieceCount() == PIECES_PER_PLAYER);
    }

    @Override
    public boolean isCurrentTurnDone() {
        return possibleTransitions.isEmpty();
    }
}
