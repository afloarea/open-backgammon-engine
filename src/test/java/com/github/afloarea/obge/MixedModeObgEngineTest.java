package com.github.afloarea.obge;

import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.engines.HybridObgEngine;
import com.github.afloarea.obge.layout.ColumnsFactory;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.utils.EngineUtils;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MixedModeObgEngineTest {

    @Test
    void testSimpleTransition() {
        final var engine = EngineUtils.buildDefault(HybridObgEngine.class);
        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(2, 1));

        final var desiredBoard = ColumnsFactory.buildBoardSnapshot(new int[][]{
                { 0, 1, 1, 0, 0, -5,    0, -3, 0, 0, 0,  5},
                {-2, 0, 0, 0, 0,  5,    0,  3, 0, 0, 0, -5}
        });

        final var transitions = engine.transitionTo(Direction.CLOCKWISE, desiredBoard);
        assertEquals(2, transitions.size());
        assertTrue(transitions.contains(new ObgTransition("A", "C", 2, null)));
        assertTrue(transitions.contains(new ObgTransition("A", "B", 1, null)));
    }

}
