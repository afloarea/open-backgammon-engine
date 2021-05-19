package com.github.afloarea.obge.impl;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.layout.BoardColumn;
import com.github.afloarea.obge.layout.ColumnArrangement;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class BoardEngineFactory {
    public static final String[][] DEFAULT_BOARD_TEMPLATE = {
            "ABCDEFGHIJKL".split(""),
            new StringBuilder("MNOPQRSTUVWX").reverse().toString().split(""),
            {"SB", "SW", "CB", "CW"}
    };

    private BoardEngineFactory() {
    }

    public static BasicObgEngine buildDefaultWithTemplate(String[][] template) {
        checkTemplate(template);
        return build(template, new int[][]{
                        {-2, 0, 0, 0, 0, +5, 0, +3, 0, 0, 0, -5},
                        {+2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, +5}
                },
                0, 0, 0, 0);
    }

    private static void checkTemplate(String[][] template) {
        if (template == null || template.length != 3 || template[0].length != 12 || template[1].length != 12 || template[2].length != 4) {
            throw new IllegalArgumentException("Template must consist of 3 arrays of length 12, 12 and 4");
        }
        final var count = Stream.concat(
                Arrays.stream(template[0]), Stream.concat(Arrays.stream(template[1]), Arrays.stream(template[2])))
                .distinct()
                .count();
        if (count != 28) {
            throw new IllegalArgumentException("Template entries must be unique");
        }
    }

    public static BasicObgEngine build(int[][] values,
                                       int suspendedForward, int suspendedBackwards, int collectedForward, int collectedBackwards) {
        return build(DEFAULT_BOARD_TEMPLATE, values, suspendedForward, suspendedBackwards, collectedForward, collectedBackwards);
    }

    public static BasicObgEngine build(String[][] template,
                                       int[][] values,
                                       int suspendedForward, int suspendedBackwards, int collectedForward, int collectedBackwards) {

        final var suspendedForwardColumn = new BoardColumn(suspendedForward, Direction.CLOCKWISE, template[2][0]);
        final var suspendedBackwardsColumn = new BoardColumn(suspendedBackwards, Direction.ANTICLOCKWISE, template[2][1]);
        final var collectedForwardColumn = new BoardColumn(collectedForward, Direction.CLOCKWISE, template[2][2]);
        final var collectedBackwardsColumn = new BoardColumn(collectedBackwards, Direction.ANTICLOCKWISE, template[2][3]);

        final ColumnSequence columnSequence = new ColumnArrangement(translateToColumns(template, values),
                suspendedForwardColumn, suspendedBackwardsColumn, collectedForwardColumn, collectedBackwardsColumn);

        return new BasicObgEngine(columnSequence);
    }

    public static BasicObgEngine build(int[][] values) {
        return build(values, 0, 0, 0, 0);
    }

    private static List<BoardColumn> translateToColumns(String[][] template, int[][] values) {
        final int[] upper = values[0];
        final int[] lower = reverse(values[1]);

        return Stream.concat(buildColumnStream(upper, template[0]), buildColumnStream(lower, template[1]))
                .collect(Collectors.toList());
    }

    private static Stream<BoardColumn> buildColumnStream(int[] rawValues, String[] template) {
        return IntStream.range(0, rawValues.length)
                .mapToObj(index ->
                        new BoardColumn(Math.abs(rawValues[index]), Direction.ofSign(rawValues[index]), template[index]));
    }

    private static int[] reverse(int[] array) {
        final int[] result = new int[array.length];
        for (int index = 0; index < array.length; index++) {
            int complement = array.length - 1 - index;
            result[index] = array[complement];
        }
        return result;
    }
}
