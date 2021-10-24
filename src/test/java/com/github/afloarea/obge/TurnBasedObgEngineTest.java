package com.github.afloarea.obge;

import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.engines.BoardStatePredictingObgEngine;
import com.github.afloarea.obge.engines.HybridObgEngine;
import com.github.afloarea.obge.layout.ColumnsFactory;
import com.github.afloarea.obge.utils.EngineUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class TurnBasedObgEngineTest {

    private static Stream<Arguments> turnBased() {
        return Stream.of(Arguments.of(BoardStatePredictingObgEngine.class), Arguments.of(HybridObgEngine.class));
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testSimpleRoll(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type);
        final var diceRoll = DiceRoll.of(2, 1);

        engine.applyDiceRoll(Direction.CLOCKWISE, diceRoll);
        final var desiredBoard = ColumnsFactory.buildBoardSnapshot(new int[][]{
                { 0, 1, 1, 0, 0,  5,    0,  3, 0, 0, 0, -5},
                {-2, 0, 0, 0, 0, -5,    0, -3, 0, 0, 0,  5}
        });
        engine.chooseBoard(Direction.CLOCKWISE, desiredBoard);

        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testDoublesHandling(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type);
        final var diceRoll = DiceRoll.of(2, 2);

        engine.applyDiceRoll(Direction.CLOCKWISE, diceRoll);
        final var desiredBoard = ColumnsFactory.buildBoardSnapshot(new int[][]{
                { 1, 0, 0, 0, 0,  5,    0,  3, 1, 0, 0, -5},
                {-2, 0, 0, 0, 0, -5,    0, -3, 0, 0, 0,  5}
        });
        engine.chooseBoard(Direction.CLOCKWISE, desiredBoard);

        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testEnterAndMove(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                {2, 2, 2, 2, 2, 2,      5, -13, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0,      0,   0, 0, 0, 0, 0}
        }, 1, 0, 0, 0);
        final var diceRoll = DiceRoll.of(1, 6);

        engine.applyDiceRoll(Direction.CLOCKWISE, diceRoll);
        final var desiredBoard = ColumnsFactory.buildBoardSnapshot(new int[][]{
                {2, 2, 2, 2, 2, 2,      6, -13, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0,      0,   0, 0, 0, 0, 0}
        });
        engine.chooseBoard(Direction.CLOCKWISE, desiredBoard);

        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testUnableToEnter(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                        { 2, -2, -2, -2, -2, -2,     -5, 13, 0, 0, 0, 0},
                        { 0,  0,  0,  0,  0,  0,      0,  0, 0, 0, 0, 0}
                },
                1, 0, 0, 0
        );
        final var diceRoll = DiceRoll.of(6, 6);

        engine.applyDiceRoll(Direction.CLOCKWISE, diceRoll);

        assertTrue(engine.getBoardChoices().isEmpty());
        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testSuspend(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][] {
                new int[]{ 2, 0, 0, 0, -1, -4,      0, -3, 0, 0, -1,  5},
                new int[]{-2, 0, 0, 0,  0,  5,      0,  3, 0, 0,  0, -4}
            }
        );
        final var diceRoll = DiceRoll.of(4, 1);

        engine.applyDiceRoll(Direction.CLOCKWISE, diceRoll);
        final var boardAfterSuspend = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{ 0, 1, 0, 0, 1, -4,      0, -3, 0, 0, -1,  5},
                new int[]{-2, 0, 0, 0, 0,  5,      0,  3, 0, 0,  0, -4}
        }, 0, 1, 0, 0);
        engine.chooseBoard(Direction.CLOCKWISE, boardAfterSuspend);

        assertTrue(engine.getBoardChoices().isEmpty());
        assertTrue(engine.isCurrentTurnDone());

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(6, 6));
        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testEnterWithSuspend(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][] {
                new int[]{-2, -2, -1, -2, -2, -2,       0, -2, 0, 0, 0, 0},
                new int[]{ 3, -2,  0,  0,  0,  5,       0,  0, 0, 0, 0, 5}
            }, 2, 0, 0, 0
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(4, 3));
        assertEquals(1, engine.getBoardChoices().size());
        
        final var firstTurn = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{-2, -2, 1, -2, -2, -2,       0, -2, 0, 0, 0, 0},
                new int[]{ 3, -2, 0,  0,  0,  5,       0,  0, 0, 0, 0, 5}
        }, 1, 1, 0, 0);

        engine.chooseBoard(Direction.CLOCKWISE, firstTurn);
        assertTrue(engine.isCurrentTurnDone());

        engine.applyDiceRoll(Direction.ANTICLOCKWISE, DiceRoll.of(6, 2));
        final var secondTurn = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{-2, -2, 1, -2, -2, -2,       0, -2, 0, 0, 0, 0},
                new int[]{ 3, -2, 0,  0,  0,  5,       0, -1, 0, 0, 0, 5}
        }, 1, 0, 0, 0);
        assertTrue(engine.getBoardChoices().contains(secondTurn));

    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testGameWon(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][] {
                new int[]{-1, 0, 0, 0, 0, 0,        0, 0, 0, 0, 0, 0},
                new int[]{ 0, 1, 1, 0, 0, 0,        0, 0, 0, 0, 0, 0}
            }, 0, 0, 13, 14
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 2));

        final var firstTurn = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{-1, 0, 0, 0, 0, 0,        0, 0, 0, 0, 0, 0},
                new int[]{ 0, 0, 0, 0, 0, 0,        0, 0, 0, 0, 0, 0}
        }, 0, 0, 15, 14);

        engine.chooseBoard(Direction.CLOCKWISE, firstTurn);
        assertTrue(engine.isCurrentTurnDone());
        assertTrue(engine.isGameComplete());
        Assertions.assertSame(Direction.CLOCKWISE, engine.getWinningDirection());

    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void collectWithHigh(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-4, -4, -4, -3, 0, 0,     0, 0, 0, 0, 0, 0},
                new int[]{ 5,  2,  4,  4, 0, 0,     0, 0, 0, 0, 0, 0}
            }
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(6, 5));
        final var firstTurn = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{-4, -4, -4, -3, 0, 0,     0, 0, 0, 0, 0, 0},
                new int[]{ 5,  2,  4,  4, 0, 0,     0, 0, 0, 0, 0, 0}
        }, 0, 0, 2, 0);

        assertDoesNotThrow(() -> engine.chooseBoard(Direction.CLOCKWISE, firstTurn));
        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testMoveAndCollect(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                new int[]{-2, -2, 0, -1, -1, -7,    0, 0, 0, 0, 0, 0},
                new int[]{ 0,  0, 1,  5,  2,  6,    0, 1, 0, 0, 0, 0}
            },
                0, 0, 0, 2
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 5));
        final var firstTurn = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{-2, -2, 0, -1, -1, -7,    0, 0, 0, 0, 0, 0},
                new int[]{ 0,  0, 1,  5,  2,  6,    0, 0, 0, 0, 0, 0}
        }, 0, 0, 3, 0);
        engine.chooseBoard(Direction.CLOCKWISE, firstTurn);

        assertTrue(engine.isCurrentTurnDone());
    }

    @ParameterizedTest
    @MethodSource("turnBased")
    void testForcedMove(Class<? extends TurnBasedObgEngine> type) {
        final var engine = EngineUtils.buildDefault(type, new int[][]{
                        new int[]{1, -2, -2, 0, -2, -2,     0, 0, 0, -2, 0, 0},
                        new int[]{0,  0,  0, 1,  0,  0,     0, 0, 0,  0, 0, 0}
                }, 0, 0, 13, 5
        );

        engine.applyDiceRoll(Direction.CLOCKWISE, DiceRoll.of(3, 6));
        final var firstTurn = ColumnsFactory.buildBoardSnapshot(new int[][] {
                new int[]{0, -2, -2, 0, -2, -2,     1, 0, 0, -2, 0, 0},
                new int[]{1,  0,  0, 0,  0,  0,     0, 0, 0,  0, 0, 0}
        }, 0, 0, 13, 5);

        assertEquals(Set.of(firstTurn), engine.getBoardChoices());
    }
}
