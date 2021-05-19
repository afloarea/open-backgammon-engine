package com.github.afloarea.obge;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;

import static com.github.afloarea.obge.common.Constants.MAX_DICE;
import static com.github.afloarea.obge.common.Constants.MIN_DICE;

/**
 * Class used to represent the dice values used by a move.
 * Can have from 0 to 4 values.
 */
public final class DiceValues implements Iterable<Integer> {
    public static final DiceValues NONE = new DiceValues(new int[0]);

    private static final int MIN_SIZE = 1;
    private static final int MAX_SIZE = 4;

    private final int[] wrappedValues;

    public static DiceValues of(List<Integer> values) {
        if (values == null || values.size() > MAX_SIZE || values.size() < MIN_SIZE) {
            throw new IllegalArgumentException(
                    "The list must have between " + MIN_SIZE + " and " + MAX_SIZE + " values");
        }
        return new DiceValues(values.stream().mapToInt(x -> x).toArray());
    }

    public static DiceValues of(int value1) {
        return ofVar(value1);
    }

    public static DiceValues of(int value1, int value2) {
        return ofVar(value1, value2);
    }

    public static DiceValues of(int value1, int value2, int value3) {
        if (value1 != value2 && value2 != value3) {
            throw new IllegalArgumentException("All values must be the same");
        }
        return ofVar(value1, value2, value3);
    }

    public static DiceValues of(int value1, int value2, int value3, int value4) {
        if (value1 != value2 && value2 != value3 && value3 != value4) {
            throw new IllegalArgumentException("All values must be the same");
        }
        return ofVar(value1, value2, value3, value4);
    }

    private static DiceValues ofVar(int... values) {
        return new DiceValues(values);
    }

    private DiceValues(int[] wrappedValues) {
        if (notWithinBounds(wrappedValues)) {
            throw new IllegalArgumentException("Dice values must be between 1 and 6");
        }
        this.wrappedValues = wrappedValues;
    }

    private static boolean notWithinBounds(int[] wrappedValues) {
        return Arrays.stream(wrappedValues).anyMatch(v -> v < MIN_DICE || v > MAX_DICE);
    }

    @Override
    public Iterator<Integer> iterator() {
        return stream().iterator();
    }

    public IntStream stream() {
        return Arrays.stream(wrappedValues);
    }

    public boolean contains(int diceValue) {
        return stream().anyMatch(value -> value == diceValue);
    }

    public int size() {
        return wrappedValues.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiceValues)) return false;
        DiceValues integers = (DiceValues) o;
        return Arrays.equals(wrappedValues, integers.wrappedValues);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(wrappedValues);
    }

    @Override
    public String toString() {
        return Arrays.toString(wrappedValues);
    }
}
