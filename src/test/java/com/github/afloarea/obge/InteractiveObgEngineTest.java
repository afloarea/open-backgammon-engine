package com.github.afloarea.obge;

import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.dice.DiceValues;
import com.github.afloarea.obge.engines.HybridObgEngine;
import com.github.afloarea.obge.engines.InteractiveTurnSlicingObgEngine;
import com.github.afloarea.obge.moves.ObgMove;
import com.github.afloarea.obge.utils.EngineUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractiveObgEngineTest {

    private static Stream<Arguments> interactiveEngines() {
        return Stream.of(Arguments.of(InteractiveTurnSlicingObgEngine.class), Arguments.of(HybridObgEngine.class));
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void boardHandlesSimpleRoll(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type);

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(2, 1));
        engine.execute(Direction.CLOCKWISE, "A", "B");
        engine.execute(Direction.CLOCKWISE, "A", "C");

        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void boardHandlesDouble(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type);

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(2, 2));
        engine.execute(Direction.CLOCKWISE, "A", "C");
        engine.execute(Direction.CLOCKWISE, "C", "E");
        engine.execute(Direction.CLOCKWISE, "E", "G");
        engine.execute(Direction.CLOCKWISE, "G", "I");

        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testCanEnter(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                {2, -2, -2, -2, -2, -2,      -5, 13, 0, 0, 0, 0},
                {0,  0,  0,  0,  0,  0,       0,  0, 0, 0, 0, 0}
        }, 1, 0, 0, 0);

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(1, 6));
        assertTrue(engine.getPossibleMoves().stream().anyMatch(move -> "SB".equals(move.source()) && "A".equals(move.target())));
        engine.execute(Direction.CLOCKWISE, "SB", "A");
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void unableToEnter(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                { 2, -2, -2, -2, -2, -2,     -5, 13, 0, 0, 0, 0},
                { 0,  0,  0,  0,  0,  0,      0,  0, 0, 0, 0, 0}
            }, 1, 0, 0, 0
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 6));
        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testSuspend(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{ 2, 0, 0, 0, -1, -4,      0, -3, 0, 0, -1,  5},
                new int[]{-2, 0, 0, 0,  0,  5,      0,  3, 0, 0,  0, -4}
            }
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(4, 1));
        engine.execute(Direction.CLOCKWISE, "A", "E");
        engine.execute(Direction.CLOCKWISE, "A", "B");
        assertTrue(engine.isCurrentTurnDone());

        final var secondDice = DiceRoll.of(6, 6);

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, secondDice);
        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testEnterWithSuspend(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-2, -2, -1, -2, -2, -2,       0, -2, 0, 0, 0, 0},
                new int[]{ 3, -2,  0,  0,  0,  5,       0,  0, 0, 0, 0, 5}
            }, 2, 0, 0, 0
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(4, 3));
        final var enterWith3 = ObgMove.of("SB", "C", DiceValues.of(3));
        Assertions.assertEquals(Set.of(enterWith3), engine.getPossibleMoves());
        engine.execute(Direction.CLOCKWISE, enterWith3.source(), enterWith3.target());
        assertTrue(engine.isCurrentTurnDone());


        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(6, 2));
        final var enterWith2 = ObgMove.of("SW", "N", DiceValues.of(2));
        assertTrue(engine.getPossibleMoves().contains(enterWith2));
        engine.execute(Direction.ANTICLOCKWISE, enterWith2.source(), enterWith2.target());
        Assertions.assertFalse(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testGameWon(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-1, 0, 0, 0, 0, 0,        0, 0, 0, 0, 0, 0},
                new int[]{ 0, 1, 1, 0, 0, 0,        0, 0, 0, 0, 0, 0}
            }, 0, 0, 13, 14
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 2));
        engine.execute(Direction.CLOCKWISE, "N", "CB");
        engine.execute(Direction.CLOCKWISE, "O", "CB");
        assertTrue(engine.isCurrentTurnDone());
        assertTrue(engine.isGameComplete());
        Assertions.assertSame(Direction.CLOCKWISE, engine.getWinningDirection());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void collectWithHigh(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-4, -4, -4, -3, 0, 0,     0, 0, 0, 0, 0, 0},
                new int[]{ 5,  2,  4,  4, 0, 0,     0, 0, 0, 0, 0, 0}
            }
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 5));
        Assertions.assertEquals(
                Set.of(ObgMove.of("P", "CB", DiceValues.of(6)),
                        ObgMove.of("P", "CB", DiceValues.of(5))),
                engine.getPossibleMoves());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testMoveAndCollect(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-2, -2, 0, -1, -1, -7,    0, 0, 0, 0, 0, 0},
                new int[]{ 0,  0, 1,  5,  2,  6,    0, 1, 0, 0, 0, 0}
            }, 0, 0, 0, 2
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 5));
        engine.execute(Direction.CLOCKWISE, "T", "Q");
        engine.execute(Direction.CLOCKWISE, "Q", "CB");

        assertTrue(engine.isCurrentTurnDone());
        assertTrue(engine.getPossibleMoves().isEmpty());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testForcedMove(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{1, -2, -2, 0, -2, -2,     0, 0, 0, -2, 0, 0},
                new int[]{0,  0,  0, 1,  0,  0,     0, 0, 0,  0, 0, 0}
            }, 0, 0, 13, 5
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 6));

        Assertions.assertEquals(
                Set.of(ObgMove.of("A", "G", DiceValues.of(6)), ObgMove.of("P", "M", DiceValues.of(3))),
                engine.getPossibleMoves());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testNonForcedMove(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-2, -4, -3, 0, 0, 0,      0, 0, 0, 0, 0,  1},
                new int[]{ 2,  3,  2, 2, 2, 3,      0, 0, 0, 0, 0, -1}
            }, 0, 0, 0, 5
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 1));

        final var availableMoves = engine.getPossibleMoves();
        assertTrue(availableMoves.contains(ObgMove.of("L", "X", DiceValues.of(1))));
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testNonForcedWithCollect(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-6, -2, -2, -2, -2, 0,    0, 0, 0, 0, 0, 0},
                new int[]{ 2,  4,  0, -1,  3, 5,    1, 0, 0, 0, 0, 0}
            }, 0, 0, 0, 0
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(2, 1));
        engine.execute(Direction.CLOCKWISE, "S", "R");
        final var availableMoves = engine.getPossibleMoves();
        assertTrue(availableMoves.contains(ObgMove.of("N", "CB", DiceValues.of(2))));
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testNonForceWithCollect2(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-6, -2, -2, -2, -2, 0,        0, 0, 0, 0, 0, 0},
                new int[]{ 2,  9,  0, -1,  3, 0,        0, 1, 0, 0, 0, 0}
            }, 0, 0, 0, 0
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 2));
        assertTrue(engine.getPossibleMoves().contains(ObgMove.of("T", "R", DiceValues.of(2))));
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testCompositeMove(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-2, 0, 0, 0, 0, -5,        0, -2, 1, 0, 1,  5},
                new int[]{ 0, 0, 0, 0, 2,  4,       -2,  2, 0, 0, 0, -4}
        });

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(6, 5));
        engine.execute(Direction.ANTICLOCKWISE, "X", "B");
        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testCompositeNoCollect(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{0, 0, -3, -3, -2, -4,     -3, 0, 0, 0, 0, 0},
                new int[]{0, 5,  2,  1,  3,  3,      0, 1, 0, 0, 0, 0}
        });

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 3));

        assertTrue(
                engine.getPossibleMoves().stream().noneMatch(move -> move.target().equals("CB")));

    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testExecuteCollectWithHigh(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{0, -1, -2, -2, -4, 0,     0, 0, 0, 0, 0, 0},
                new int[]{0,  0,  1,  1,  2, 8,     0, 0, 0, 0, 0, 0}
            }, 0, 0, 3, 6
        );

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(6, 2));
        final var move = ObgMove.of("E", "CW", DiceValues.of(6));
        assertTrue(engine.getPossibleMoves().contains(move));
        engine.execute(Direction.ANTICLOCKWISE, move.source(), move.target());
        Assertions.assertFalse(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testBasicMove(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{0, 0,  0,  0, 1, -5,      0, -3, 0, 0, 0,  4},
                new int[]{1, 0, -1, -1, 0,  5,      0,  4, 0, 0, 0, -5}
        });

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(4, 1));
        assertTrue(engine.getPossibleMoves().contains(ObgMove.of("O", "P", DiceValues.of(1))));
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testFinishMove(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-1, -2, 0, 0, -1, -2,         0, 0, 0, 0, 0, 0},
                new int[]{ 0,  1, 0, 0,  0,  0,         0, 0, 0, 0, 0, 0}
            }, 0, 0, 14, 9);

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(4, 1));

        engine.execute(Direction.CLOCKWISE, "N", "CB");
        assertTrue(engine.isGameComplete());
        Assertions.assertSame(Direction.CLOCKWISE, engine.getWinningDirection());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testForcedComposite(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{3,  0,  0,  0, 0, 0,      -8, 0, 0, 0, 0, 4},
                new int[]{1, -2, -1, -2, 0, 3,      -2, 4, 0, 0, 0, 0}
        });

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 3));

        Assertions.assertEquals(
                Set.of(ObgMove.of("A", "D", DiceValues.of(3)),
                        ObgMove.of("A", "J", DiceValues.of(3, 6))),
                engine.getPossibleMoves());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testForcedComposite2Columns(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{3,  0,  0, 0, 0, 0,   -9, 0, 0, 0, 0, 4},
                new int[]{1, -2, -1, 0, 0, 3,   -3, 4, 0, 0, 0, 0}
        });

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 3));

        Assertions.assertEquals(
                Set.of(ObgMove.of("A", "D", DiceValues.of(3)),
                        ObgMove.of("A", "J", DiceValues.of(3, 6)),
                        ObgMove.of("L", "V", DiceValues.of(3)),
                        ObgMove.of("L", "P", DiceValues.of(3, 6))),
                engine.getPossibleMoves());
    }

    @ParameterizedTest
    @MethodSource("interactiveEngines")
    void testUnforcedToHomeArea(Class<? extends InteractiveObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-2, -2, -1, -2, -4, -3,       0, -1, 0, 0, 0, 0},
                new int[]{ 2,  2,  1,  2,  3,  4,       0, 0, 0, 1, 0, 0}
        });

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(6, 4));

        assertTrue(engine.getPossibleMoves().containsAll(Set.of(
                ObgMove.of("H", "D", DiceValues.of(4)),
                ObgMove.of("H", "B", DiceValues.of(6)))));
    }

}
