package com.github.afloarea.obge;

import java.util.Objects;
import java.util.StringJoiner;

public final class ObgTransition {

    private final String source;
    private final String target;
    private final int usedDie;
    private final boolean suspending;
    // direction maybe?


    public ObgTransition(String source, String target, int usedDie, boolean suspending) {
        this.source = source;
        this.target = target;
        this.usedDie = usedDie;
        this.suspending = suspending;
    }

    public String getSource() {
        return source;
    }

    public String getTarget() {
        return target;
    }

    public int getUsedDie() {
        return usedDie;
    }

    public boolean isSuspending() {
        return suspending;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ObgTransition)) return false;
        ObgTransition obgTransition = (ObgTransition) o;
        return usedDie == obgTransition.usedDie && suspending == obgTransition.suspending
                && source.equals(obgTransition.source) && target.equals(obgTransition.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, target, usedDie, suspending);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ObgTransition.class.getSimpleName() + "[", "]")
                .add("source='" + source + "'")
                .add("target='" + target + "'")
                .add("usedDie=" + usedDie)
                .add("suspending=" + suspending)
                .toString();
    }
}
