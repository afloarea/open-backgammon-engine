package com.github.afloarea.obge.predictors;

import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.layout.BoardMapper;
import com.github.afloarea.obge.moves.ObgTransition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class BoardBySequencePredictor extends
        AbstractObgPredictor<Map<List<ObgTransition>, BoardSnapshot>, Map<List<ObgTransition>, BoardSnapshot>> {

    public BoardBySequencePredictor() {
        super(new HashMap<>());
    }

    @Override
    protected void clearAggregator() {
        aggregator.clear();
    }

    @Override
    protected Map<List<ObgTransition>, BoardSnapshot> mapAggregatorToResult() {
        final int maxMoves = aggregator.keySet().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        return aggregator.entrySet().stream()
                .filter(entry -> entry.getKey().size() == maxMoves)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    protected void save() {
        if (performedMoves.isEmpty()) {
            return;
        }
        aggregator.put(List.copyOf(performedMoves), BoardMapper.takeSnapshot(columns));
    }
}
