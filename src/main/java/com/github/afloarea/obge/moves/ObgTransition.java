package com.github.afloarea.obge.moves;

public record ObgTransition(String source, String target, int usedDie, String suspended) { // direction maybe?

    public boolean isSuspending() {
        return suspended != null;
    }

}
