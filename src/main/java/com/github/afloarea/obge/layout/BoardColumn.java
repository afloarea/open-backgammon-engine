package com.github.afloarea.obge.layout;

import com.github.afloarea.obge.Direction;
import com.github.afloarea.obge.ColumnInfo;
import com.github.afloarea.obge.exceptions.IllegalObgActionException;

import java.util.Objects;

public final class BoardColumn implements ColumnInfo {

    private int pieceCount;
    private Direction elementsDirection;
    private final String id;

    public BoardColumn(int pieceCount, Direction elementDirection, String id) {
        this.pieceCount = pieceCount;
        this.elementsDirection = pieceCount == 0 ? Direction.NONE : elementDirection;
        this.id = id;
    }

    @Override
    public int getPieceCount() {
        return pieceCount;
    }

    @Override
    public String getId() {
        return id;
    }

    public boolean isEmpty() {
        return this.pieceCount == 0;
    }

    @Override
    public Direction getMovingDirectionOfElements() {
        return elementsDirection;
    }

    public void addElement(Direction elementDirection) {
        if (pieceCount == 0) {
            this.elementsDirection = elementDirection;
            pieceCount++;
            return;
        }
        if (this.elementsDirection != elementDirection || elementDirection == Direction.NONE) {
            throw new IllegalObgActionException("Cannot add element of direction " + elementDirection);
        }
        pieceCount++;
    }

    public void removeElement() {
        if (pieceCount == 0) {
            throw new IllegalObgActionException("Cannot remove non-existing pieces from column " + id);
        }
        if (--pieceCount == 0) {
            elementsDirection = Direction.NONE;
        }
    }

    public boolean isClearForDirection(Direction direction) {
        return this.pieceCount <= 1 || this.elementsDirection == direction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BoardColumn)) return false;
        BoardColumn that = (BoardColumn) o;
        return pieceCount == that.pieceCount && id.equals(that.id) && elementsDirection == that.elementsDirection;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceCount, elementsDirection, id);
    }
}
