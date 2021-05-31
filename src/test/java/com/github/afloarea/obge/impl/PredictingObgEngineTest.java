package com.github.afloarea.obge.impl;

import com.github.afloarea.obge.DiceRoll;
import com.github.afloarea.obge.DiceValues;
import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ObgMove;
import com.github.afloarea.obge.factory.BoardTemplate;
import com.github.afloarea.obge.factory.ColumnsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class PredictingObgEngineTest {



    @Test
    void boardHandlesSimpleRoll() {
        final var board = create();
        final var diceResult = DiceRoll.of(2, 1);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        board.execute(Direction.CLOCKWISE, "A", "B");
        board.execute(Direction.CLOCKWISE, "A", "C");

        assertTrue(board.isCurrentTurnDone());
    }

    @Test
    void boardHandlesDouble() {
        final var board = create();
        final var diceResult = DiceRoll.of(2, 2);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        board.execute(Direction.CLOCKWISE, "A", "C");
        board.execute(Direction.CLOCKWISE, "C", "E");
        board.execute(Direction.CLOCKWISE, "E", "G");
        board.execute(Direction.CLOCKWISE, "G", "I");

        assertTrue(board.isCurrentTurnDone());
    }

    @Test
    void testCanEnter() {
        final var board = create(new int[][]{
                {-2, +2, +2, +2, +2, +2, +5, -13, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
        }, 1, 0, 0, 0);
        final var diceResult = DiceRoll.of(1, 6);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        assertTrue(board.getPossibleMoves().stream().anyMatch(move -> "SB".equals(move.getSource()) && "A".equals(move.getTarget())));
        board.execute(Direction.CLOCKWISE, "SB", "A");
    }

    @Test
    void unableToEnter() {
        final var board = create(new int[][]{
                        {-2, +2, +2, +2, +2, +2, +5, -13, 0, 0, 0, 0},
                        {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                },
                1, 0, 0, 0
        );
        final var diceResult = DiceRoll.of(6, 6);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        assertTrue(board.isCurrentTurnDone());
    }

    @Test
    void testSuspend() {
        final var board = create(new int[][]{
                new int[]{-2, 0, 0, 0, 1, 4, 0, 3, 0, 0, 1, -5},
                new int[]{+2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 4}
        });
        final var diceResult = DiceRoll.of(4, 1);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        board.execute(Direction.CLOCKWISE, "A", "E");
        board.execute(Direction.CLOCKWISE, "A", "B");
        assertTrue(board.isCurrentTurnDone());

        final var secondDice = DiceRoll.of(6, 6);

        board.applyDiceRoll(Direction.ANTICLOCKWISE, secondDice);
        assertTrue(board.isCurrentTurnDone());
    }

    @Test
    void testEnterWithSuspend() {
        final var board = create(new int[][]{
                        new int[]{2, 2, 1, 2, 2, 2, 0, 2, 0, 0, 0, 0},
                        new int[]{-3, 2, 0, 0, 0, -5, 0, 0, 0, 0, 0, -5}
                },
                2, 0, 0, 0);
        final var diceResult = DiceRoll.of(4, 3);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        final var enterWith3 = ObgMove.of("SB", "C", DiceValues.of(3));
        Assertions.assertEquals(Set.of(enterWith3), board.getPossibleMoves());
        board.execute(Direction.CLOCKWISE, enterWith3);
        assertTrue(board.isCurrentTurnDone());

        final var secondDice = DiceRoll.of(6, 2);

        board.applyDiceRoll(Direction.ANTICLOCKWISE, secondDice);
        final var enterWith2 = ObgMove.of("SW", "N", DiceValues.of(2));
        assertTrue(board.getPossibleMoves().contains(enterWith2));
        board.execute(Direction.ANTICLOCKWISE, enterWith2);
        Assertions.assertFalse(board.isCurrentTurnDone());
    }

    @Test
    void testGameWon() {
        final var board = create(new int[][]{
                        new int[]{1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                        new int[]{0, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                },
                0, 0, 13, 14);
        final var diceResult = DiceRoll.of(3, 2);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        board.execute(Direction.CLOCKWISE, "N", "CB");
        board.execute(Direction.CLOCKWISE, "O", "CB");
        assertTrue(board.isCurrentTurnDone());
        assertTrue(board.isGameComplete());
        Assertions.assertSame(Direction.CLOCKWISE, board.getWinningDirection());
    }

    @Test
    void collectWithHigh() {
        final var board = create(new int[][]{
                new int[]{4, 4, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0},
                new int[]{-5, -2, -4, -4, 0, 0, 0, 0, 0, 0, 0, 0}
        });
        final var diceResult = DiceRoll.of(6, 5);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        Assertions.assertEquals(
                Set.of(ObgMove.of("P", "CB", DiceValues.of(6)),
                        ObgMove.of("P", "CB", DiceValues.of(5))),
                board.getPossibleMoves());
    }

    @Test
    void testMoveAndCollect() {
        final var board = create(new int[][]{
                        new int[]{2, 2, 0, 1, 1, 7, 0, 0, 0, 0, 0, 0},
                        new int[]{0, 0, -1, -5, -2, -6, 0, -1, 0, 0, 0, 0}
                },
                0, 0, 0, 2);
        final var diceResult = DiceRoll.of(3, 5);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        board.execute(Direction.CLOCKWISE, "T", "Q");
        board.execute(Direction.CLOCKWISE, "Q", "CB");

        assertTrue(board.isCurrentTurnDone());
        assertTrue(board.getPossibleMoves().isEmpty());
    }

    @Test
    void testForcedMove() {
        final var board = create(new int[][]{
                        new int[]{-1, 2, 2, 0, 2, 2, 0, 0, 0, 2, 0, 0},
                        new int[]{0, 0, 0, -1, 0, 0, 0, 0, 0, 0, 0, 0}
                },
                0, 0, 13, 5);
        final var diceResult = DiceRoll.of(3, 6);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);

        Assertions.assertEquals(
                Set.of(ObgMove.of("A", "G", DiceValues.of(6)), ObgMove.of("P", "M", DiceValues.of(3))),
                board.getPossibleMoves());
    }

    @Test
    void testNonForcedMove() {
        final var board = create(new int[][]{
                        new int[]{2, 4, 3, 0, 0, 0, 0, 0, 0, 0, 0, -1},
                        new int[]{-2, -3, -2, -2, -2, -3, 0, 0, 0, 0, 0, 1}
                },
                0, 0, 0, 5);
        final var diceResult = DiceRoll.of(6, 1);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);

        final var availableMoves = board.getPossibleMoves();
        assertTrue(availableMoves.contains(ObgMove.of("L", "X", DiceValues.of(1))));
    }

    @Test
    void testNonForcedWithCollect() {
        final var board = create(new int[][]{
                        new int[]{6, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0},
                        new int[]{-2, -4, 0, 1, -3, -5, -1, 0, 0, 0, 0, 0}
                },
                0, 0, 0, 0);
        final var diceResult = DiceRoll.of(2, 1);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        board.execute(Direction.CLOCKWISE, ObgMove.of("S", "R", DiceValues.of(1)));
        final var availableMoves = board.getPossibleMoves();
        assertTrue(availableMoves.contains(ObgMove.of("N", "CB", DiceValues.of(2))));
    }

    @Test
    void testNonForceWithCollect2() {
        final var board = create(new int[][]{
                new int[]{6, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0},
                new int[]{-2, -9, 0, 1, -3, 0, 0, -1, 0, 0, 0, 0}
        });
        final var diceResult = DiceRoll.of(6, 2);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);
        assertTrue(board.getPossibleMoves().contains(ObgMove.of("T", "R", DiceValues.of(2))));
    }

    @Test
    void testCompositeMove() {
        final var board = create(new int[][]{
                new int[]{2, 0, 0, 0, 0, 5, 0, 2, -1, 0, -1, -5},
                new int[]{0, 0, 0, 0, -2, -4, 2, -2, 0, 0, 0, 4},
        });
        final var diceResult = DiceRoll.of(6, 5);

        board.applyDiceRoll(Direction.ANTICLOCKWISE, diceResult);
        board.execute(Direction.ANTICLOCKWISE, "X", "B");
        assertTrue(board.isCurrentTurnDone());
    }

    @Test
    void testCompositeNoCollect() {
        final var board = create(new int[][]{
                new int[]{0, 0, 3, 3, 2, 4, 3, 0, 0, 0, 0, 0},
                new int[]{0, -5, -2, -1, -3, -3, 0, -1, 0, 0, 0, 0}
        });
        final var diceResult = DiceRoll.of(3, 3);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);

        assertTrue(
                board.getPossibleMoves().stream().noneMatch(move -> move.getTarget().equals("CB")));

    }

    @Test
    void testExecuteCollectWithHigh() {
        final var board = create(new int[][]{
                        new int[]{0, 1, 2, 2, 4, 0, 0, 0, 0, 0, 0, 0},
                        new int[]{0, 0, -1, -1, -2, -8, 0, 0, 0, 0, 0, 0}
                },
                0, 0, 3, 6);
        final var diceResult = DiceRoll.of(6, 2);

        board.applyDiceRoll(Direction.ANTICLOCKWISE, diceResult);
        final var move = ObgMove.of("E", "CW", DiceValues.of(6));
        assertTrue(board.getPossibleMoves().contains(move));
        board.execute(Direction.ANTICLOCKWISE, move);
        Assertions.assertFalse(board.isCurrentTurnDone());
    }

    @Test
    void testBasicMove() {
        final var board = create(new int[][]{
                new int[]{0, 0, 0, 0, -1, 5, 0, 3, 0, 0, 0, -4},
                new int[]{-1, 0, 1, 1, 0, -5, 0, -4, 0, 0, 0, 5}
        });
        final var diceResult = DiceRoll.of(4, 1);

        board.applyDiceRoll(Direction.ANTICLOCKWISE, diceResult);
        assertTrue(board.getPossibleMoves().contains(ObgMove.of("O", "P", DiceValues.of(1))));
    }

    @Test
    void testFinishMove() {
        final var board = create(new int[][]{
                        new int[]{1, 2, 0, 0, 1, 2, 0, 0, 0, 0, 0, 0},
                        new int[]{0, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}
                },
                0, 0, 14, 9);
        final var diceResult = DiceRoll.of(4, 1);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);

        board.execute(Direction.CLOCKWISE, "N", "CB");
        assertTrue(board.isGameComplete());
        Assertions.assertSame(Direction.CLOCKWISE, board.getWinningDirection());
    }

    @Test
    void testForcedComposite() {
        final var board = create(new int[][]{
                new int[]{-3, 0, 0, 0, 0, 0, 8, 0, 0, 0, 0, -4},
                new int[]{-1, 2, 1, 2, 0, -3, 2, -4, 0, 0, 0, 0}
        });
        final var diceResult = DiceRoll.of(6, 3);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);

        Assertions.assertEquals(
                Set.of(ObgMove.of("A", "D", DiceValues.of(3)),
                        ObgMove.of("A", "J", DiceValues.of(3, 6))),
                board.getPossibleMoves());
    }

    @Test
    void testForcedComposite2Columns() {
        final var board = create(new int[][]{
                new int[]{-3, 0, 0, 0, 0, 0, 9, 0, 0, 0, 0, -4},
                new int[]{-1, 2, 1, 0, 0, -3, 3, -4, 0, 0, 0, 0}
        });
        final var diceResult = DiceRoll.of(6, 3);

        board.applyDiceRoll(Direction.CLOCKWISE, diceResult);

        Assertions.assertEquals(
                Set.of(ObgMove.of("A", "D", DiceValues.of(3)),
                        ObgMove.of("A", "J", DiceValues.of(3, 6)),
                        ObgMove.of("L", "V", DiceValues.of(3)),
                        ObgMove.of("L", "P", DiceValues.of(3, 6))),
                board.getPossibleMoves());
    }

    @Test
    void testUnforcedToHomeArea() {
        final var board = create(new int[][]{
                new int[]{2, 2, 1, 2, 4, 3, 0, 1, 0, 0, 0, 0},
                new int[]{-2, -2, -1, -2, -3, -4, 0, 0, 0, -1, 0, 0}
        });
        final var diceResult = DiceRoll.of(6, 4);

        board.applyDiceRoll(Direction.ANTICLOCKWISE, diceResult);

        assertTrue(board.getPossibleMoves().containsAll(Set.of(
                ObgMove.of("H", "D", DiceValues.of(4)),
                ObgMove.of("H", "B", DiceValues.of(6)))));
    }

    private PredictingObgEngine create(int[][] layout) {
        return new PredictingObgEngine(ColumnsFactory.buildColumnSequence(layout));
    }

    private PredictingObgEngine create(int[][] layout, int suspendedForward, int suspendedBackwards, int collectedForward, int collectedBackwards) {
        return new PredictingObgEngine(ColumnsFactory.buildColumnSequence(BoardTemplate.getDefault(), layout, suspendedForward, suspendedBackwards, collectedForward, collectedBackwards));
    }

    private PredictingObgEngine create() {
        return new PredictingObgEngine(ColumnsFactory.buildStartingSequence());
    }

}
