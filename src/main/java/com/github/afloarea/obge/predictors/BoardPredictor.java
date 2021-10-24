package com.github.afloarea.obge.predictors;

import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.layout.BoardMapper;
import com.github.afloarea.obge.moves.ObgTransition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class BoardPredictor extends AbstractObgPredictor<Set<BoardSnapshot>, Map<BoardSnapshot, List<ObgTransition>>> {

    public BoardPredictor() {
        super(new HashMap<>());
    }

    @Override
    protected void clearAggregator() {
        aggregator.clear();
    }

    @Override
    protected Set<BoardSnapshot> mapAggregatorToResult() {
        final var maxMoves = aggregator.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        return aggregator.entrySet().stream()
                .filter(entry -> entry.getValue().size() == maxMoves)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    protected void save() {
        if (performedMoves.isEmpty()) {
            return;
        }
        aggregator.put(BoardMapper.takeSnapshot(columns), List.copyOf(performedMoves));
    }
}
