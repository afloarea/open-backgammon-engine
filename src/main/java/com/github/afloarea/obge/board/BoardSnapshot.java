package com.github.afloarea.obge.board;

import com.github.afloarea.obge.Direction;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A snapshot of the backgammon board.
 */
public final class BoardSnapshot {

    private final ColumnSnapshot[] columns;
    private final int clockwiseCollected;
    private final int anticlockwiseCollected;
    private final int clockwiseSuspended;
    private final int anticlockwiseSuspended;

    private BoardSnapshot(Builder builder) {
        columns = builder.columns;
        clockwiseCollected = builder.clockwiseCollected;
        anticlockwiseCollected = builder.anticlockwiseCollected;
        clockwiseSuspended = builder.clockwiseSuspended;
        anticlockwiseSuspended = builder.anticlockwiseSuspended;
    }

    public static Builder builder() {
        return new Builder();
    }

    public int getClockwiseCollected() {
        return clockwiseCollected;
    }

    public int getAnticlockwiseCollected() {
        return anticlockwiseCollected;
    }

    public int getClockwiseSuspended() {
        return clockwiseSuspended;
    }

    public int getAnticlockwiseSuspended() {
        return anticlockwiseSuspended;
    }

    public int getCollected(Direction direction) {
        if (direction == Direction.CLOCKWISE) {
            return clockwiseCollected;
        }
        if (direction == Direction.ANTICLOCKWISE) {
            return anticlockwiseCollected;
        }
        throw new IllegalArgumentException("Invalid direction provided");
    }

    public int getSuspended(Direction direction) {
        if (direction == Direction.CLOCKWISE) {
            return clockwiseSuspended;
        }
        if (direction == Direction.ANTICLOCKWISE) {
            return anticlockwiseSuspended;
        }
        throw new IllegalArgumentException("Invalid direction provided");
    }

    /**
     * Stream regular columns (non-suspended, non-collected) in the given direction.
     * @param direction the direction
     * @return the stream of columns
     */
    public Stream<ColumnSnapshot> stream(Direction direction) {
        return switch (direction) {
            case NONE -> throw new IllegalArgumentException("Invalid direction");
            case CLOCKWISE -> Arrays.stream(columns);
            case ANTICLOCKWISE -> IntStream.iterate(columns.length - 1, index -> index >= 0, index -> index - 1)
                    .mapToObj(index -> columns[index]);
        };
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardSnapshot)) return false;
        BoardSnapshot that = (BoardSnapshot) o;
        return clockwiseCollected == that.clockwiseCollected
                && anticlockwiseCollected == that.anticlockwiseCollected
                && clockwiseSuspended == that.clockwiseSuspended
                && anticlockwiseSuspended == that.anticlockwiseSuspended
                && Arrays.equals(columns, that.columns);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(clockwiseCollected, anticlockwiseCollected, clockwiseSuspended, anticlockwiseSuspended);
        result = 31 * result + Arrays.hashCode(columns);
        return result;
    }

    @Override
    public String toString() {
        return "BoardSnapshot{" +
                "columns=" + Arrays.toString(columns) +
                ", clockwiseCollected=" + clockwiseCollected +
                ", anticlockwiseCollected=" + anticlockwiseCollected +
                ", clockwiseSuspended=" + clockwiseSuspended +
                ", anticlockwiseSuspended=" + anticlockwiseSuspended +
                '}';
    }


    public static final class Builder {
        private ColumnSnapshot[] columns;
        private int clockwiseCollected = 0;
        private int anticlockwiseCollected = 0;
        private int clockwiseSuspended = 0;
        private int anticlockwiseSuspended = 0;

        public Builder withColumns(ColumnSnapshot[] columns) {
            this.columns = columns;
            return this;
        }

        public Builder withClockwiseCollected(int clockwiseCollected) {
            this.clockwiseCollected = clockwiseCollected;
            return this;
        }

        public Builder withAnticlockwiseCollected(int anticlockwiseCollected) {
            this.anticlockwiseCollected = anticlockwiseCollected;
            return this;
        }

        public Builder withClockwiseSuspended(int clockwiseSuspended) {
            this.clockwiseSuspended = clockwiseSuspended;
            return this;
        }

        public Builder withAnticlockwiseSuspended(int anticlockwiseSuspended) {
            this.anticlockwiseSuspended = anticlockwiseSuspended;
            return this;
        }

        public BoardSnapshot build() {
            return new BoardSnapshot(this);
        }
    }
}
