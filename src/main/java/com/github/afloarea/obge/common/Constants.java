package com.github.afloarea.obge.common;

import com.github.afloarea.obge.dice.DiceRoll;

import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

public final class Constants {
    public static final int BOARD_COLUMNS       = 24;
    public static final int PIECES_PER_PLAYER   = 15;
    public static final int SUSPEND_INDEX       = 0;
    public static final int COLLECT_INDEX       = 25;
    public static final int HOME_START          = 19;

    public static final int MIN_DICE = 1;
    public static final int MAX_DICE = 6;

    public static final List<DiceRoll> DICE_ROLLS = IntStream.rangeClosed(Constants.MIN_DICE, Constants.MAX_DICE)
            .mapToObj(firstDice -> IntStream.rangeClosed(firstDice, Constants.MAX_DICE)
                    .mapToObj(secondDice -> new DiceRoll(firstDice, secondDice)))
            .flatMap(Function.identity())
            .toList();

    private Constants() {}
}
