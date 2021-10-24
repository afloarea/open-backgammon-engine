package com.github.afloarea.obge.predictors;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.layout.ColumnSequence;

public interface ObgPredictor<R> {

    R predict(ColumnSequence columns, DiceRoll diceRoll, Direction direction);

}
