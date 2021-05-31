package com.github.afloarea.obge.layout;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.factory.BoardTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.github.afloarea.obge.Direction.ANTICLOCKWISE;
import static com.github.afloarea.obge.Direction.CLOCKWISE;

public final class ColumnsFactory {

    public static ColumnSequence buildStartingSequence(BoardTemplate template) {
        return buildColumnSequence(template, new int[][]{
                {-2, 0, 0, 0, 0, +5, 0, +3, 0, 0, 0, -5},
                {+2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, +5}
        });
    }

    public static ColumnSequence buildStartingSequence() {
        return buildStartingSequence(BoardTemplate.getDefault());
    }

    public static ColumnSequence buildColumnSequence(int[][] values) {
        return buildColumnSequence(BoardTemplate.getDefault(), values);
    }

    public static ColumnSequence buildColumnSequence(BoardTemplate template, int[][] values) {
        return buildColumnSequence(template, values, 0, 0, 0, 0);
    }

    public static ColumnSequence buildColumnSequence(BoardTemplate template,
                                                     int[][] values,

                                                     int suspendedForward, int suspendedBackwards,
                                                     int collectedForward, int collectedBackwards) {
        final var forwardSuspend =
                new BoardColumn(suspendedForward, CLOCKWISE, template.getSuspendId(CLOCKWISE));
        final var backwardsSuspend =
                new BoardColumn(suspendedBackwards, ANTICLOCKWISE, template.getSuspendId(ANTICLOCKWISE));
        final var collectedForwardColumn =
                new BoardColumn(collectedForward, CLOCKWISE, template.getCollectId(CLOCKWISE));
        final var collectedBackwardsColumn =
                new BoardColumn(collectedBackwards, ANTICLOCKWISE, template.getCollectId(ANTICLOCKWISE));

        return new ColumnArrangement(translateToColumns(template, values),
                forwardSuspend, backwardsSuspend, collectedForwardColumn, collectedBackwardsColumn);
    }

    private static List<BoardColumn> translateToColumns(BoardTemplate template, int[][] values) {
        final int[] upper = values[0];
        final int[] lower = values[1];

        final var upperColumns = buildColumnList(upper, template.getUpperRow());
        final var lowerColumns = buildColumnList(lower, template.getLowerRow());

        Collections.reverse(lowerColumns);
        upperColumns.addAll(lowerColumns);
        return upperColumns;
    }

    private static List<BoardColumn> buildColumnList(int[] rawValues, List<String> template) {
        return IntStream.range(0, rawValues.length)
                .mapToObj(index -> new BoardColumn(
                        Math.abs(rawValues[index]), Direction.ofSign(rawValues[index]), template.get(index)))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private ColumnsFactory() {
    }

}
