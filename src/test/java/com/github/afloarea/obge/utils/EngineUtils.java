package com.github.afloarea.obge.utils;

import com.github.afloarea.obge.ObgEngine;
import com.github.afloarea.obge.engines.BoardStatePredictingObgEngine;
import com.github.afloarea.obge.engines.HybridObgEngine;
import com.github.afloarea.obge.engines.InteractiveTurnSlicingObgEngine;
import com.github.afloarea.obge.factory.BoardTemplate;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.layout.ColumnsFactory;

public final class EngineUtils {

    public static <E extends ObgEngine> E buildDefault(Class<E> type) {
        return buildEngine(type, ColumnsFactory.buildStartingSequence());
    }

    public static <E extends ObgEngine> E buildDefault(Class<E> type, int[][] composition) {
        return buildEngine(type, ColumnsFactory.buildColumnSequence(composition));
    }

    public static <E extends ObgEngine> E buildDefault(Class<E> type, int[][] composition,
                                                       int forwardSuspended, int backwardsSuspended,
                                                       int forwardCollect, int backwardsCollect) {
        return buildEngine(type, ColumnsFactory.buildColumnSequence(BoardTemplate.getDefault(),
                composition, forwardSuspended, backwardsSuspended, forwardCollect, backwardsCollect));
    }

    public static <E extends ObgEngine> E buildEngine(Class<E> engineClass, ColumnSequence columnSequence) {
        if (engineClass == InteractiveTurnSlicingObgEngine.class) {
            return engineClass.cast(new InteractiveTurnSlicingObgEngine(columnSequence));
        }
        if (engineClass == BoardStatePredictingObgEngine.class) {
            return engineClass.cast(new BoardStatePredictingObgEngine(columnSequence));
        }
        if (engineClass == HybridObgEngine.class) {
            return engineClass.cast(new HybridObgEngine(columnSequence));
        }
        throw new IllegalArgumentException("No implementation");
    }

    private EngineUtils() {
    }
}
