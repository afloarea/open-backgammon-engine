package com.github.afloarea.obge.expander;

import com.github.afloarea.obge.board.BoardSnapshot;

import java.util.List;
import java.util.Objects;

public final class StateNode<V> {

    private final int layer;
    private final BoardSnapshot board;
    private V value;
    private List<StateNode<V>> children;

    StateNode(int layer, BoardSnapshot board) {
        this.layer = layer;
        this.board = board;
    }

    public int getLayer() {
        return layer;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }

    public List<StateNode<V>> getChildren() {
        return children;
    }

    void setChildren(List<StateNode<V>> children) {
        this.children = children;
    }

    public BoardSnapshot getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof StateNode<?> stateNode)) return false;
        return layer == stateNode.layer
                && Objects.equals(value, stateNode.value)
                && Objects.equals(children, stateNode.children)
                && Objects.equals(board, stateNode.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(layer, value, children, board);
    }

    @Override
    public String toString() {
        return "StateNode{" +
                "layer=" + layer +
                ", value=" + value +
                ", children=" + children +
                '}';
    }
}
