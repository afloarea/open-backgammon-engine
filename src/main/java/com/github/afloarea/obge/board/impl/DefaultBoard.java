package com.github.afloarea.obge.board.impl;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.board.ColumnInfo;
import com.github.afloarea.obge.board.ObgBoard;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.utils.MoveUtils;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Collectors;

public final class DefaultBoard implements ObgBoard {

    private final ColumnSequence columns;

    public DefaultBoard(ColumnSequence columns) {
        this.columns = columns;
    }

    @Override
    public void doSequence(Direction direction, List<ObgTransition> sequence) {
        sequence.forEach(transition -> MoveUtils.doTransition(transition, direction, columns));
    }

    @Override
    public void undoSequence(Direction direction, List<ObgTransition> sequence) {
        final var seq = new ArrayDeque<>(sequence);
        seq.descendingIterator().forEachRemaining(transition -> {
            final var source = columns.getColumnById(transition.getSource());
            final var target = columns.getColumnById(transition.getTarget());
            source.addElement(direction);
            target.removeElement();
            if (transition.isSuspending()) {
                target.addElement(direction.reverse());
                columns.getSuspendedColumn(direction.reverse()).removeElement();
            }
        });
    }

    @Override
    public ColumnInfo getSuspendColumn(Direction direction) {
        return columns.getSuspendedColumn(direction);
    }

    @Override
    public ColumnInfo getCollectColumn(Direction direction) {
        return columns.getCollectColumn(direction);
    }

    @Override
    public List<ColumnInfo> getNormalColumns(Direction direction) {
        return columns.stream(direction).skip(1).collect(Collectors.toList());
    }

    @Override
    public void reset() {
        columns.reset();
    }
}
