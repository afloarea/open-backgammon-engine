package com.github.afloarea.obge.expander;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.common.Constants;
import com.github.afloarea.obge.layout.ColumnsFactory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class StateExpanderTest {

    @Test
    void testSimpleExpand() {
        final var board = ColumnsFactory.buildBoardSnapshot(new int[][] {
                { 1, 0, 0, 0, 0, 0,      0, 0, 0, 0 ,0, 0},
                {-1, 0, 0, 0 ,0 ,0,      0, 0, 0, 0, 0, 0},
        }, 0, 0, 14, 14);

        final long expectedUniqueStates = Constants.DICE_ROLLS.stream()
                .mapToInt(roll -> roll.isSimple() ? roll.dice1() + roll.dice2() : 4 * roll.dice1())
                .distinct()
                .count();

        final class Counter {
            static int value = 0;
        }
        final var expander = StateExpander.<Void>builder()
                .withLeafConsumer(node -> Counter.value++)
                .build();

        expander.expand(board, Direction.CLOCKWISE, 1);
        Assertions.assertEquals(expectedUniqueStates, Counter.value);
    }

    @Test
    void test2LayersSimple() {
        final var board = ColumnsFactory.buildBoardSnapshot(new int[][] {
                {-5, 0, 0, 0, 0, 0,      1, 0, 0, 0 ,0, 0},
                { 0, 0, 0, 0 ,0 ,0,      0, 0, 0, 0, 0, 0},
        }, 0, 0, 14, 10);

        final long expectedClockwiseMoves = Constants.DICE_ROLLS.stream()
                .mapToInt(roll -> roll.isSimple() ? roll.dice1() + roll.dice2() : 4 * roll.dice1())
                .distinct()
                .count() - 1; // 6-6 and 5-5 produce the same result
        final long expectedAnticlockwiseMoves = 2; // either remains with 3 pieces or 1 piece
        final long totalExpected = expectedAnticlockwiseMoves * expectedClockwiseMoves;

        final class Counter {
            static int value = 0;
        }
        final var expander = StateExpander.<Void>builder()
                .withLeafConsumer(node -> Counter.value++)
                .build();

        expander.expand(board, Direction.ANTICLOCKWISE, 2);
        Assertions.assertEquals(totalExpected, Counter.value);
    }
}
