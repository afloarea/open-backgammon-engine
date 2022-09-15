package com.github.afloarea.obge.dice;

import java.util.random.RandomGenerator;
import java.util.stream.IntStream;

import static com.github.afloarea.obge.common.Constants.MAX_DICE;
import static com.github.afloarea.obge.common.Constants.MIN_DICE;

/**
 * A record representing a dice roll.
 */
public record DiceRoll(int dice1, int dice2) {
    private static final RandomGenerator RANDOM = RandomGenerator.of("Random");

    /**
     * Create an ordered (higher first) dice roll result.
     * @param dice1 the first die value
     * @param dice2 the second die value
     */
    public DiceRoll(int dice1, int dice2) {
        if (dice1 < MIN_DICE || dice1 > MAX_DICE || dice2 < MIN_DICE || dice2 > MAX_DICE) {
            throw new IllegalArgumentException("Dice values must be between " + MIN_DICE + " and " + MAX_DICE);
        }
        this.dice1 = Math.max(dice1, dice2);
        this.dice2 = Math.min(dice1, dice2);
    }

    /**
     * Create a dice roll result.
     * @param dice1 the first die value
     * @param dice2 the second die value
     * @return the result
     */
    public static DiceRoll of(int dice1, int dice2) {
        return new DiceRoll(dice1, dice2);
    }

    /**
     * Does the dice pair have the same values?
     * @return the check result
     */
    public boolean isDouble() {
        return dice1 == dice2;
    }

    /**
     * Does the dice pair have different values?
     * @return the check result
     */
    public boolean isSimple() {
        return dice1 != dice2;
    }

    /**
     * Get a stream of dice values. For simple dice, there will be 2 values, for doubles there will be 4.
     * @return the stream of values
     */
    public IntStream stream() {
        return isSimple() ? IntStream.of(dice1, dice2) : IntStream.generate(() -> dice1).limit(4);
    }

    /**
     * Throw the dice!
     * @return the dice roll result
     */
    public static DiceRoll generate() {
        final int first = MIN_DICE + RANDOM.nextInt(MAX_DICE);
        final int second = MIN_DICE + RANDOM.nextInt(MAX_DICE);
        return new DiceRoll(Math.max(first, second), Math.min(first, second));
    }
}
