package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.MixedModeObgEngine;
import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.BoardMapper;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.predictors.BoardBySequencePredictor;
import com.github.afloarea.obge.predictors.ObgPredictor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public final class HybridObgEngine extends BaseObgEngine implements MixedModeObgEngine {

    private final Map<List<ObgTransition>, BoardSnapshot> predictions = new HashMap<>();
    private final ObgPredictor<Map<List<ObgTransition>, BoardSnapshot>> predictor = new BoardBySequencePredictor();

    public HybridObgEngine(ColumnSequence columns) {
        super(columns);
    }

    @Override
    public void applyDiceRoll(Direction direction, DiceRoll dice) {
        validateDirection(direction);

        currentDirection = direction;
        predictions.clear();
        predictions.putAll(predictor.predict(columns, dice, direction));
    }

    @Override
    public boolean isCurrentTurnDone() {
        return predictions.isEmpty();
    }

    @Override
    public List<ObgTransition> execute(Direction direction, String source, String target) {
        checkTransitionPossible(direction);

        final var viableSequences = predictions.keySet().stream()
                .filter(sequence -> sequenceHasSourceAndTarget(sequence, source, target))
                .toList();

        if (viableSequences.isEmpty()) {
            throw new IllegalObgActionException("Invalid move provided");
        }

        final var executionSequence = extractUpToTarget(viableSequences.get(0), target);

        executeSequence(executionSequence);

        record Prediction(List<ObgTransition> sequence, BoardSnapshot snapshot) {
        }
        final var newPredictions = viableSequences.stream()
                .mapMulti((List<ObgTransition> sequence, Consumer<Prediction> mapper) -> {
                    final var newSequence = sequence.stream()
                            .dropWhile(transition -> !transition.target().equals(target))
                            .skip(1) // skip the target as well
                            .toList();
                    if (!newSequence.isEmpty()) {
                        mapper.accept(new Prediction(newSequence, predictions.get(sequence)));
                    }
                })
                .collect(Collectors.toMap(Prediction::sequence, Prediction::snapshot));

        predictions.clear();
        predictions.putAll(newPredictions);

        return executionSequence;
    }

    @Override
    public Set<ObgMove> getPossibleMoves() {
        return predictions.keySet().stream()
                .mapMulti(this::sequenceToMultipleMoves)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Set<BoardSnapshot> getBoardChoices() {
        return predictions.values().stream().collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public BoardSnapshot chooseBoard(Direction playingDirection, BoardSnapshot board) {
        checkTransitionPossible(playingDirection);

        if (!predictions.containsValue(board)) {
            throw new IllegalObgActionException("Invalid board provided");
        }

        BoardMapper.loadSnapshot(columns, board);
        predictions.clear();
        return board;
    }

    @Override
    public List<ObgTransition> transitionTo(Direction playingDirection, BoardSnapshot boardSnapshot) {
        checkTransitionPossible(playingDirection);
        final var transition = predictions.entrySet().stream()
                .filter(entry -> Objects.equals(entry.getValue(), boardSnapshot))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow(() -> new IllegalObgActionException("Invalid board provided"));

        BoardMapper.loadSnapshot(columns, boardSnapshot);
        predictions.clear();
        return transition;
    }

    @Override
    public BoardSnapshot getCurrentBoard() {
        return BoardMapper.takeSnapshot(columns);
    }
}
