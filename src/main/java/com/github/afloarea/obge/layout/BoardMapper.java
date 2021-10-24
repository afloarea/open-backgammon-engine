package com.github.afloarea.obge.layout;

import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.board.ColumnSnapshot;
import com.github.afloarea.obge.common.Constants;

import static com.github.afloarea.obge.Direction.ANTICLOCKWISE;
import static com.github.afloarea.obge.Direction.CLOCKWISE;

public final class BoardMapper {

    public static BoardSnapshot takeSnapshot(ColumnSequence columnSequence) {
        final ColumnSnapshot[] columnEntries = columnSequence.stream(CLOCKWISE).skip(1)
                .map(column -> new ColumnSnapshot(column.getPieceCount(), column.getMovingDirectionOfElements()))
                .toArray(ColumnSnapshot[]::new);

        return BoardSnapshot.builder()
                .withAnticlockwiseCollected(columnSequence.getCollectColumn(ANTICLOCKWISE).getPieceCount())
                .withClockwiseCollected(columnSequence.getCollectColumn(CLOCKWISE).getPieceCount())
                .withAnticlockwiseSuspended(columnSequence.getSuspendedColumn(ANTICLOCKWISE).getPieceCount())
                .withClockwiseSuspended(columnSequence.getSuspendedColumn(CLOCKWISE).getPieceCount())
                .withColumns(columnEntries)
                .build();

    }

    public static void loadSnapshot(ColumnSequence sequence, BoardSnapshot snapshot) {
        sequence.getCollectColumn(CLOCKWISE).set(CLOCKWISE, snapshot.getClockwiseCollected());
        sequence.getSuspendedColumn(CLOCKWISE).set(CLOCKWISE, snapshot.getClockwiseSuspended());

        sequence.getCollectColumn(ANTICLOCKWISE).set(ANTICLOCKWISE, snapshot.getAnticlockwiseCollected());
        sequence.getSuspendedColumn(ANTICLOCKWISE).set(ANTICLOCKWISE, snapshot.getAnticlockwiseSuspended());

        final var columns = sequence.stream(CLOCKWISE).skip(1).toArray(BoardColumn[]::new);
        final var snapshotColumns = snapshot.stream(CLOCKWISE).toArray(ColumnSnapshot[]::new);

        for (int index = 0; index < Constants.BOARD_COLUMNS; index++) {
            final var snapshotColumn = snapshotColumns[index];
            columns[index].set(snapshotColumn.elementsDirection(), snapshotColumn.pieceCount());
        }
    }

    private BoardMapper() {
    }
}
