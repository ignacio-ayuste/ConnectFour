package com.game.connectfour.model;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Board {

    public static final int NUMBER_OF_COLUMNS = 7;
    public static final int NUMBER_OF_ROWS = 6;

    @ApiModelProperty(value = "List of the checkers on the board", required = true)
    private List<Field> fields;
    @ApiModelProperty(value = "Last player's move")
    private Field lastPopulatedField;

    public Board() {
        this.fields = new ArrayList<>(NUMBER_OF_COLUMNS * NUMBER_OF_ROWS);

        for (int column = 0; column < NUMBER_OF_COLUMNS; column++) {
            for (int row = 0; row < NUMBER_OF_ROWS; row++) {
                this.fields.add(new Field(new Position(column, row)));
            }
        }
    }

    public List<Field> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public Field getLastPopulatedField() {
        return lastPopulatedField;
    }

    public void setLastPopulatedField(Field lastPopulatedField) {
        this.lastPopulatedField = lastPopulatedField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Board board = (Board) o;
        return Objects.equals(fields, board.fields) &&
                Objects.equals(lastPopulatedField, board.lastPopulatedField);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fields, lastPopulatedField);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("fields", fields)
                .add("lastPopulatedField", lastPopulatedField)
                .toString();
    }
}