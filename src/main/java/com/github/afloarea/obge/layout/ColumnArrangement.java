package com.github.afloarea.obge.layout;

import com.github.afloarea.obge.Direction;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.github.afloarea.obge.common.Constants.COLLECT_INDEX;

/**
 * A symbolic representation of the column within a backgammon game.
 * Each column has an alphabetic id, starting with A from the upper left corner,
 * ending with L in the upper right, and then continuing with M from lower left to X to the lower right.
 * <p>
 * The columns for suspended pieces have an index of 0 and an id starting with S.
 * The columns for collected pieces have an index of 25 and an id starting with C.
 * The rest of the columns have an index in the [1, 24] inclusive.
 * <p>
 * This implementation provides constant time for most of its operations.
 */
public final class ColumnArrangement implements ColumnSequence {

    private final Map<String, BoardColumn> columnsById;
    private final Map<Direction, BoardColumn[]> columnsByDirection;
    private final Map<Direction, Map<String, Integer>> columnPositionByIdByDirection = new EnumMap<>(Direction.class);

    @Override
    public BoardColumn getColumn(int index, Direction direction) {
        return columnsByDirection.get(direction)[index];
    }

    @Override
    public Stream<BoardColumn> stream(Direction direction) {
        return Arrays.stream(columnsByDirection.get(direction)).limit(COLLECT_INDEX);
    }

    @Override
    public int countPiecesUpToIndex(int index, Direction direction) {
        return Arrays.stream(columnsByDirection.get(direction))
                .limit(index)
                .filter(column -> column.getMovingDirectionOfElements() == direction)
                .mapToInt(BoardColumn::getPieceCount)
                .sum();
    }

    @Override
    public int getColumnIndex(BoardColumn column, Direction direction) {
        return columnPositionByIdByDirection.get(direction).get(column.getId());
    }

    @Override
    public BoardColumn getColumnById(String columnId) {
        return columnsById.get(columnId);
    }

    /**
     * Construct a column arrangement.
     *
     * @param columnLayout           the columnLayout containing the normal columns (with ids from A to X)
     * @param clockwiseSuspended     the clockwise direction
     * @param anticlockwiseSuspended the anticlockwise direction
     * @param clockwiseCollected     the clockwise direction
     * @param anticlockwiseCollected the anticlockwise direction
     */
    public ColumnArrangement(List<BoardColumn> columnLayout,
                             BoardColumn clockwiseSuspended, BoardColumn anticlockwiseSuspended,
                             BoardColumn clockwiseCollected, BoardColumn anticlockwiseCollected) {
        final var base = new ArrayList<>(columnLayout);

        final var clockwise = new ArrayDeque<>(base);
        clockwise.addFirst(clockwiseSuspended);
        clockwise.addLast(clockwiseCollected);

        Collections.reverse(base);
        final var anticlockwise = new ArrayDeque<>(base);
        anticlockwise.addFirst(anticlockwiseSuspended);
        anticlockwise.addLast(anticlockwiseCollected);

        columnsByDirection = Map.of(
                Direction.CLOCKWISE, clockwise.toArray(BoardColumn[]::new),
                Direction.ANTICLOCKWISE, anticlockwise.toArray(BoardColumn[]::new));

        Stream.of(Direction.CLOCKWISE, Direction.ANTICLOCKWISE).forEach(direction -> {
            final Map<String, Integer> columnIdByIndex = new HashMap<>();

            final var columns = columnsByDirection.get(direction);
            IntStream.range(0, columns.length).forEach(index -> columnIdByIndex.put(columns[index].getId(), index));

            columnPositionByIdByDirection.put(direction, Collections.unmodifiableMap(columnIdByIndex));
        });

        columnsById = Stream.concat(
                clockwise.stream(), Stream.of(anticlockwiseSuspended, anticlockwiseCollected))
                .collect(Collectors.toMap(BoardColumn::getId, Function.identity()));

    }

    @Override
    public void reset() {
        columnsById.values().forEach(BoardColumn::clear);
        final var clockwiseColumns = columnsByDirection.get(Direction.CLOCKWISE);
        clockwiseColumns[1].set(Direction.CLOCKWISE, 2);
        clockwiseColumns[6].set(Direction.ANTICLOCKWISE, 5);
        clockwiseColumns[8].set(Direction.ANTICLOCKWISE, 3);
        clockwiseColumns[12].set(Direction.CLOCKWISE, 5);
        clockwiseColumns[13].set(Direction.ANTICLOCKWISE, 5);
        clockwiseColumns[17].set(Direction.CLOCKWISE, 3);
        clockwiseColumns[19].set(Direction.CLOCKWISE, 5);
        clockwiseColumns[24].set(Direction.ANTICLOCKWISE, 2);
    }
}
