package com.github.afloarea.obge.impl;

import com.github.afloarea.obge.factory.BoardTemplate;
import com.github.afloarea.obge.factory.ColumnsFactory;

public final class BoardEngineFactory {

    private BoardEngineFactory() {
    }

    public static BasicObgEngine build(int[][] values,
                                       int suspendedForward, int suspendedBackwards, int collectedForward, int collectedBackwards) {
        return build(BoardTemplate.getDefault(), values, suspendedForward, suspendedBackwards, collectedForward, collectedBackwards);
    }

    public static BasicObgEngine build(BoardTemplate template,
                                       int[][] values,
                                       int suspendedForward, int suspendedBackwards, int collectedForward, int collectedBackwards) {
        final var columnSequence = ColumnsFactory.buildColumnSequence(
                template, values, suspendedForward, suspendedBackwards, collectedForward, collectedBackwards);

        return new BasicObgEngine(columnSequence);
    }

    public static BasicObgEngine build(int[][] values) {
        return build(values, 0, 0, 0, 0);
    }
}
