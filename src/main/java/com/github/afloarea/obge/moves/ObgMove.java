package com.github.afloarea.obge.moves;

import com.github.afloarea.obge.dice.DiceValues;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A model of a backgammon move.
 */
public record ObgMove(String source, String target, DiceValues diceValues) {

    public ObgMove {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        Objects.requireNonNull(diceValues);
    }

    public static ObgMove of(String source, String target, DiceValues diceValues) {
        return new ObgMove(source, target, diceValues);
    }

    @Override
    public String toString() {
        return new StringJoiner(",", "M{", "}")
                .add("s=" + source)
                .add("t=" + target)
                .add("dv=" + diceValues)
                .toString();
    }
}
