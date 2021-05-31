package com.github.afloarea.obge.factory;

import com.github.afloarea.obge.InteractiveObgEngine;
import com.github.afloarea.obge.MixedModeObgEngine;
import com.github.afloarea.obge.TurnBasedObgEngine;
import com.github.afloarea.obge.layout.ColumnsFactory;
import com.github.afloarea.obge.engines.BasicObgEngine;
import com.github.afloarea.obge.engines.PredictingObgEngine;

public final class ObgEngines {

    public InteractiveObgEngine newInteractive(BoardTemplate template) {
        return new BasicObgEngine(ColumnsFactory.buildStartingSequence(template));
    }

    public TurnBasedObgEngine newTurnBased(BoardTemplate template) {
        return new PredictingObgEngine(ColumnsFactory.buildStartingSequence(template));
    }

    public MixedModeObgEngine newMixed(BoardTemplate template) {
        return new PredictingObgEngine(ColumnsFactory.buildStartingSequence(template));
    }

    private ObgEngines() {}
}
