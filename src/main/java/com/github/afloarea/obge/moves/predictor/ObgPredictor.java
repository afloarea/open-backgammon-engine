package com.github.afloarea.obge.moves.predictor;

import com.github.afloarea.obge.dice.DiceRoll;
import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.moves.ObgTransition;
import com.github.afloarea.obge.layout.ColumnSequence;

import java.util.List;
import java.util.Set;

public interface ObgPredictor {

    Set<List<ObgTransition>> predict(ColumnSequence columns, DiceRoll diceRoll, Direction direction);

}
