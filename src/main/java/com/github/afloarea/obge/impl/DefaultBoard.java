package com.github.afloarea.obge.impl;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ObgTransition;
import com.github.afloarea.obge.ColumnInfo;
import com.github.afloarea.obge.ObgBoard;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.ArrayDeque;
import java.util.List;
import java.util.stream.Collectors;

public final class DefaultBoard implements ObgBoard {

    private final ColumnSequence columns;

    public DefaultBoard(ColumnSequence columns) {
        this.columns = columns;
    }

    @Override
    public void doSequence(List<ObgTransition> sequence, Direction direction) {
        sequence.forEach(transition -> {
            final var source = columns.getColumnById(transition.getSource());
            final var target = columns.getColumnById(transition.getTarget());
            if (transition.isSuspending()) {
                target.removeElement();
                columns.getSuspendedColumn(direction.reverse()).addElement(direction.reverse());
            }
            target.addElement(direction);
            source.removeElement();
        });
    }

    @Override
    public void undoSequence(List<ObgTransition> sequence, Direction direction) {
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
}
