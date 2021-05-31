package com.github.afloarea.obge;

import com.github.afloarea.obge.moves.ObgTransition;

import java.util.List;
import java.util.Set;

public interface TurnBasedObgEngine extends ObgEngine {

    Set<List<ObgTransition>> getPossibleSequences();

    List<ObgTransition> selectSequence(List<ObgTransition> transition);

}
