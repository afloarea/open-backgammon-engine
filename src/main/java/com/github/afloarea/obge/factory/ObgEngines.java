package com.github.afloarea.obge.factory;

import com.github.afloarea.obge.InteractiveObgEngine;
import com.github.afloarea.obge.MixedModeObgEngine;
import com.github.afloarea.obge.ObgEngine;
import com.github.afloarea.obge.TurnBasedObgEngine;
import com.github.afloarea.obge.engines.*;
import com.github.afloarea.obge.layout.ColumnsFactory;

/**
 * Factory for creating ObgEngines.
 */
public final class ObgEngines {

    /**
     * Create a new engine.
     *
     * @param engineType the engine type,
     *                   one of {@link InteractiveObgEngine}, {@link TurnBasedObgEngine}, {@link MixedModeObgEngine}
     * @param template   the template of the board
     * @param <E>        the type
     * @return the engine
     * @throws IllegalArgumentException if an invalid engine type is provided
     */
    public static <E extends ObgEngine> E create(Class<E> engineType, BoardTemplate template) {
        return engineType.cast(createEngineOfType(engineType, template));
    }

    private static <E> Object createEngineOfType(Class<E> type, BoardTemplate template) {
        if (type == InteractiveObgEngine.class) {
            return new InteractiveTurnSlicingObgEngine(ColumnsFactory.buildStartingSequence(template));
        }
        if (type == TurnBasedObgEngine.class) {
            return new BoardStatePredictingObgEngine(ColumnsFactory.buildStartingSequence(template));
        }
        if (type == MixedModeObgEngine.class) {
            return new HybridObgEngine(ColumnsFactory.buildStartingSequence(template));
        }
        throw new IllegalArgumentException("No engine for type " + type);
    }

    private ObgEngines() {
    }
}
