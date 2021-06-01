package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.MixedModeObgEngine;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.dice.DiceValues;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.moves.executor.DefaultMoveExecutor;
import com.github.afloarea.obge.moves.executor.MoveExecutor;
import com.github.afloarea.obge.moves.predictor.DefaultPredictor;
import com.github.afloarea.obge.moves.predictor.ObgPredictor;
import com.github.afloarea.obge.moves.utils.MoveUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.afloarea.obge.common.Constants.PIECES_PER_PLAYER;
import static java.util.function.Predicate.not;

public final class PredictingObgEngine extends BaseObgEngine implements MixedModeObgEngine {

    private final ObgPredictor predictor;
    private final MoveExecutor moveExecutor;

    private final Set<List<ObgTransition>> possibleTransitions = new HashSet<>();

    public PredictingObgEngine(ColumnSequence columns) {
        super(columns);
        this.predictor = new DefaultPredictor();
        this.moveExecutor = new DefaultMoveExecutor(columns);
    }

    @Override
    public void applyDiceRoll(Direction direction, DiceRoll dice) {
        validateDirection(direction);

        currentDirection = direction;
        possibleTransitions.addAll(predictor.predict(columns, dice, direction));
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

    @Override
    public Set<List<ObgTransition>> getPossibleSequences() {
        return Set.copyOf(possibleTransitions);
    }

    @Override
    public List<ObgTransition> selectSequence(List<ObgTransition> transition) {
        if (!possibleTransitions.contains(transition)) {
            throw new IllegalObgActionException("Invalid transition provided");
        }

        transition.forEach(partialTransition -> MoveUtils.doTransition(partialTransition, currentDirection, columns));

        return transition;
    }

    @Override
    public void reset() {
        super.reset();
        possibleTransitions.clear();
    }
}
