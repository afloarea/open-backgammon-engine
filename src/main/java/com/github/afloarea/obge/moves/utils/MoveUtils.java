package com.github.afloarea.obge.moves.utils;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.moves.ObgTransition;

public final class MoveUtils {

    public static void doTransition(ObgTransition transition, Direction direction, ColumnSequence columns) {
        final var source = columns.getColumnById(transition.getSource());
        final var target = columns.getColumnById(transition.getTarget());

        if (transition.isSuspending()) {
            target.removeElement();
            columns.getSuspendedColumn(direction.reverse()).addElement(direction.reverse());
        }

        source.removeElement();
        target.addElement(direction);
    }

    public MoveUtils() {}
}
