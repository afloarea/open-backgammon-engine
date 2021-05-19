package com.github.afloarea.obge;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A model of a backgammon move.
 */
public final class ObgMove {
    private final String source;
    private final String target;
    private final DiceValues diceValues;

    private ObgMove(String source, String target, DiceValues diceValues) {
        this.source = source;
        this.target = target;
        this.diceValues = diceValues;
    }

    public static ObgMove of(String source, String target, DiceValues diceValues) {
        Objects.requireNonNull(source);
        Objects.requireNonNull(target);
        Objects.requireNonNull(diceValues);
        return new ObgMove(source, target, diceValues);
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    /**
     * Dice values used for performing this move.
     * @return the used values
     */
    public DiceValues getDiceValues() {
        return diceValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObgMove)) return false;
        ObgMove obgMove = (ObgMove) o;
        return Objects.equals(source, obgMove.source)
                && Objects.equals(target, obgMove.target)
                && Objects.equals(diceValues, obgMove.diceValues);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, diceValues);
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
