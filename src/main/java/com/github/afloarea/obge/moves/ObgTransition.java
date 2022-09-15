package com.github.afloarea.obge.moves;

/**
 * Represents a single move of a piece either from the board
 * from one column to another using a single value of the die (1/2 for simple dice rolls or 1/4 for doubles).
 * @param source the source column id
 * @param target the target column id
 * @param usedDie the value of the die
 * @param suspended the suspended column id if the target had one of the opponent's piece
 */
public record ObgTransition(String source, String target, int usedDie, String suspended) { // direction maybe?

    /**
     * Did this transition cause an opponent's piece to be taken out of play temporarily?
     * @return the check result
     */
    public boolean isSuspending() {
        return suspended != null;
    }

}
