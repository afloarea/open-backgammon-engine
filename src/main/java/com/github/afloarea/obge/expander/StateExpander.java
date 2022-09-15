package com.github.afloarea.obge.expander;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.board.BoardSnapshot;
import com.github.afloarea.obge.layout.BoardMapper;
import com.github.afloarea.obge.layout.ColumnSequence;
import com.github.afloarea.obge.layout.ColumnsFactory;
import com.github.afloarea.obge.predictors.BoardPredictor;
import com.github.afloarea.obge.predictors.ObgPredictor;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.github.afloarea.obge.common.Constants.DICE_ROLLS;
import static com.github.afloarea.obge.common.Constants.PIECES_PER_PLAYER;

public final class StateExpander<V> {

    private final Consumer<StateNode<V>> leafConsumer;
    private final Consumer<StateNode<V>> parentConsumer;

    private final ColumnSequence columns;

    //TODO: caching maybe?
    private final ObgPredictor<Set<BoardSnapshot>> predictor = new BoardPredictor();

    private StateExpander(Builder<V> builder) {
        leafConsumer = builder.leafConsumer;
        parentConsumer = builder.parentConsumer;
        columns = ColumnsFactory.buildStartingSequence();
    }

    public static <G> Builder<G> builder() {
        return new Builder<>();
    }

    public V expand(BoardSnapshot root, Direction playingDirection, int layers) {
        if (layers < 0) throw new IllegalArgumentException("Negative number of layers");

        return computeNode(root, playingDirection, 0, layers).getValue();
    }

    private StateNode<V> computeNode(BoardSnapshot board, Direction playingDirection, int currentLayer, int maxLayer) {
        final var node = new StateNode<V>(currentLayer, board);
        if (currentLayer == maxLayer // max layer reached
                // or game is over
                || board.getClockwiseCollected() == PIECES_PER_PLAYER
                || board.getAnticlockwiseCollected() == PIECES_PER_PLAYER) {

            node.setChildren(Collections.emptyList());
            if (leafConsumer != null) leafConsumer.accept(node);
            return node;
        }

        BoardMapper.loadSnapshot(columns, board);
        final var possibleStates = DICE_ROLLS.stream()
                .flatMap(roll -> predictor.predict(columns, roll, playingDirection).stream())
                .collect(Collectors.toUnmodifiableSet());

        if (possibleStates.isEmpty()) {
            node.setChildren(Collections.emptyList());
            if (leafConsumer != null) leafConsumer.accept(node);
            return node;
        }

        final List<StateNode<V>> children = possibleStates.stream()
                .map(childBoard -> computeNode(childBoard, playingDirection.reverse(), currentLayer + 1, maxLayer))
                .toList();

        node.setChildren(children);
        if (parentConsumer != null) parentConsumer.accept(node);
        node.setChildren(Collections.emptyList()); // clear children
        return node;
    }

    public static final class Builder<V> {
        private Consumer<StateNode<V>> leafConsumer = null;
        private Consumer<StateNode<V>> parentConsumer = null;

        private Builder() {
        }

        public Builder<V> withLeafConsumer(Consumer<StateNode<V>> leafConsumer) {
            this.leafConsumer = leafConsumer;
            return this;
        }

        public Builder<V> withParentConsumer(Consumer<StateNode<V>> parentConsumer) {
            this.parentConsumer = parentConsumer;
            return this;
        }

        public StateExpander<V> build() {
            return new StateExpander<>(this);
        }
    }
}
