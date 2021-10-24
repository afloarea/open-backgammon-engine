package com.github.afloarea.obge;

import java.util.Random;

/**
 * The direction.
 */
public enum Direction {
    CLOCKWISE(1) {
        @Override
        public Direction reverse() {
            return ANTICLOCKWISE;
        }
    },
    ANTICLOCKWISE(-1) {
        @Override
        public Direction reverse() {
            return CLOCKWISE;
        }
    },
    NONE(0) {
        @Override
        public Direction reverse() {
            return NONE;
        }
    };

    private final int sign;

    Direction(int sign) {
        this.sign = sign;
    }

    public int getSign() {
        return sign;
    }

    /**
     * Retrieve the opposite direction.
     * @return the opposite direction
     */
    public abstract Direction reverse();

    public static Direction ofSign(int sign) {
        if (sign == 0) {
            return NONE;
        }
        return sign > 0 ? Direction.CLOCKWISE : Direction.ANTICLOCKWISE;
    }

    /**
     * Get a random direction.
     * @return a direction at random but not {@code NONE}.
     */
    public static Direction getRandom() {
        return Wrapper.RANDOM.nextBoolean() ? Direction.CLOCKWISE : Direction.ANTICLOCKWISE;
    }

    private static final class Wrapper {
        private static final Random RANDOM = new Random();
    }
}
