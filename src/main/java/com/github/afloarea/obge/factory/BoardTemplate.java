package com.github.afloarea.obge.factory;

import com.github.afloarea.obge.Direction;

import java.util.*;
import java.util.stream.Stream;

import static com.github.afloarea.obge.common.Constants.BOARD_COLUMNS;

public final class BoardTemplate {
    private final List<String> upperRowIds;
    private final List<String> lowerRowIds;
    private final Map<Direction, String> suspendColumnsIds;
    private final Map<Direction, String> collectColumnsIds;

    private BoardTemplate(Builder builder) {
        upperRowIds = List.copyOf(builder.upperRowIds);
        lowerRowIds = List.copyOf(builder.lowerRowIds);
        suspendColumnsIds = new EnumMap<>(builder.suspendColumnsIds);
        collectColumnsIds = new EnumMap<>(builder.collectColumnsIds);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static BoardTemplate getDefault() {
        return Holder.DEFAULT;
    }

    public List<String> getUpperRow() {
        return upperRowIds;
    }

    public List<String> getLowerRow() {
        return lowerRowIds;
    }

    public String getSuspendId(Direction direction) {
        return suspendColumnsIds.get(direction);
    }

    public String getCollectId(Direction direction) {
        return collectColumnsIds.get(direction);
    }

    private static final class Holder {
        private static final BoardTemplate DEFAULT = new Builder().build();
    }

    public static final class Builder {
        private static final List<String> DEFAULT_UPPER = List.of("ABCDEFGHIJKL".split(""));
        private static final List<String> DEFAULT_LOWER = List.of("MNOPQRSTUVWX".split(""));

        private final List<String> upperRowIds = new ArrayList<>();
        private final List<String> lowerRowIds = new ArrayList<>();
        private final Map<Direction, String> suspendColumnsIds = new EnumMap<>(Direction.class);
        private final Map<Direction, String> collectColumnsIds = new EnumMap<>(Direction.class);

        public Builder() {
            upperRowIds.addAll(DEFAULT_UPPER);
            lowerRowIds.addAll(DEFAULT_LOWER);
            suspendColumnsIds.put(Direction.CLOCKWISE, "SB");
            suspendColumnsIds.put(Direction.ANTICLOCKWISE, "SW");
            collectColumnsIds.put(Direction.CLOCKWISE, "CB");
            collectColumnsIds.put(Direction.ANTICLOCKWISE, "CW");
        }

        public Builder withUpperRowIds(List<String> ids) {
            this.upperRowIds.clear();
            this.upperRowIds.addAll(ids);
            return this;
        }

        public Builder withLowerRowIds(List<String> ids) {
            this.lowerRowIds.clear();
            this.lowerRowIds.addAll(ids);
            return this;
        }

        public Builder withUpperRowIds(String... ids) {
            return withUpperRowIds(Arrays.asList(ids));
        }

        public Builder withLowerRowIds(String... ids) {
            return withLowerRowIds(Arrays.asList(ids));
        }

        public Builder withSuspendId(Direction direction, String id) {
            suspendColumnsIds.put(direction, id);
            return this;
        }

        public Builder withCollectId(Direction direction, String id) {
            collectColumnsIds.put(direction, id);
            return this;
        }

        public BoardTemplate build() {
            checkTemplate(upperRowIds, lowerRowIds);
            return new BoardTemplate(this);
        }

        private static void checkTemplate(List<String> upper, List<String> lower) {
            if (upper == null || lower == null
                    || upper.size() != BOARD_COLUMNS / 2 || lower.size() != BOARD_COLUMNS / 2) {
                throw new IllegalArgumentException("Template must have " + BOARD_COLUMNS + " unique ids");
            }

            final var idCount = Stream.concat(upper.stream(), lower.stream())
                    .distinct()
                    .count();

            if (idCount != BOARD_COLUMNS) {
                throw new IllegalArgumentException("Template entries must be unique");
            }
        }
    }
}
