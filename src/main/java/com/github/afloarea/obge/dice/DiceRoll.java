package com.github.afloarea.obge.dice;

import java.util.Random;
import java.util.stream.IntStream;

import static com.github.afloarea.obge.common.Constants.MAX_DICE;
import static com.github.afloarea.obge.common.Constants.MIN_DICE;

/**
 * A record representing a dice roll.
 */
public record DiceRoll(int dice1, int dice2) {
    private static final Random RANDOM = new Random();

    public DiceRoll(int dice1, int dice2) {
        if (dice1 < MIN_DICE || dice1 > MAX_DICE || dice2 < MIN_DICE || dice2 > MAX_DICE) {
            throw new IllegalArgumentException("Dice values must be between " + MIN_DICE + " and " + MAX_DICE);
        }
        this.dice1 = Math.max(dice1, dice2);
        this.dice2 = Math.min(dice1, dice2);
    }

    public static DiceRoll of(int dice1, int dice2) {
        return new DiceRoll(dice1, dice2);
    }

    public boolean isDouble() {
        return dice1 == dice2;
    }

    public boolean isSimple() {
        return dice1 != dice2;
    }

    public IntStream stream() {
        return isSimple() ? IntStream.of(dice1, dice2) : IntStream.generate(() -> dice1).limit(4);
    }

    public static DiceRoll generate() {
        final int first = MIN_DICE + RANDOM.nextInt(MAX_DICE);
        final int second = MIN_DICE + RANDOM.nextInt(MAX_DICE);
        return new DiceRoll(Math.max(first, second), Math.min(first, second));
    }
}
