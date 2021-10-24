package com.github.afloarea.obge.engines;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.InteractiveObgEngine;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.predictors.ObgPredictor;
import com.github.afloarea.obge.predictors.SequencePredictor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

public final class InteractiveTurnSlicingObgEngine extends BaseObgEngine implements InteractiveObgEngine {

    private final Set<List<ObgTransition>> possibleSequences = new HashSet<>();
    private final ObgPredictor<Set<List<ObgTransition>>> sequencePredictor = new SequencePredictor();

    public InteractiveTurnSlicingObgEngine(ColumnSequence columns) {
        super(columns);
    }

    @Override
    public void applyDiceRoll(Direction direction, DiceRoll dice) {
        validateDirection(direction);

        currentDirection = direction;
        possibleSequences.clear();
        possibleSequences.addAll(sequencePredictor.predict(columns, dice, direction));
    }

    @Override
    public boolean isCurrentTurnDone() {
        return possibleSequences.isEmpty();
    }

    @Override
    public List<ObgTransition> execute(Direction direction, String source, String target) {
        checkTransitionPossible(direction);

        final var viableSequences = possibleSequences.stream()
                .filter(sequence -> sequenceHasSourceAndTarget(sequence, source, target))
                .toList();

        if (viableSequences.isEmpty()) {
            throw new IllegalObgActionException("Invalid move provided");
        }

        final var executionSequence = extractUpToTarget(viableSequences.get(0), target);

        executeSequence(executionSequence);

        possibleSequences.clear();
        viableSequences.stream()
                .map(sequence -> sequence.stream()
                        .dropWhile(transition -> !transition.target().equals(target))
                        .skip(1)    // skip the target as well
                        .toList())
                .filter(not(List::isEmpty))
                .forEach(possibleSequences::add);

        return executionSequence;
    }

    @Override
    public Set<ObgMove> getPossibleMoves() {
        return possibleSequences.stream()
                .mapMulti(this::sequenceToMultipleMoves)
                .collect(Collectors.toUnmodifiableSet());
    }
}
