package com.game.connectfour.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class Position {

    @ApiModelProperty(value = "Number of column", required = true)
    private int column;
    @ApiModelProperty(value = "Number of row", required = true)
    private int row;

    @JsonCreator
    Position(int column, int row) {
        this.column = column;
        this.row = row;
    }

    public int getColumn() {
        return column;
    }

    public int getRow() {
        return row;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return column == position.column &&
                row == position.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(column, row);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("column", column)
                .add("row", row)
                .toString();
    }
}