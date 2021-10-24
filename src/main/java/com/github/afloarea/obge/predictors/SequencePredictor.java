package com.github.afloarea.obge.predictors;

import com.github.afloarea.obge.moves.ObgTransition;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public final class SequencePredictor extends AbstractObgPredictor<Set<List<ObgTransition>>, Set<List<ObgTransition>>> {

    public SequencePredictor() {
        super(new HashSet<>());
    }

    @Override
    protected void clearAggregator() {
        aggregator.clear();
    }

    @Override
    protected Set<List<ObgTransition>> mapAggregatorToResult() {
        final var maxMoves = aggregator.stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        return aggregator.stream()
                .filter(sequence -> sequence.size() == maxMoves)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected void save() {
        if (performedMoves.isEmpty()) {
            return;
        }
        aggregator.add(List.copyOf(performedMoves));
    }
}
