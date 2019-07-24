package com.game.connectfour.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.PersistenceConstructor;

import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Field {

    @ApiModelProperty(value = "Position of the field on the board", required = true)
    private Position position;
    @ApiModelProperty(value = "Colour of the checker field (null means field not filled)")
    private Player.Colour colour;

    Field(Position position) {
        this(position, null);
    }

    @JsonCreator
    @PersistenceConstructor
    Field(Position position, Player.Colour colour) {
        this.position = position;
        this.colour = colour;
    }

    public Position getPosition() {
        return position;
    }

    public Player.Colour getColour() {
        return colour;
    }

    public void setColour(Player.Colour colour) {
        this.colour = colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(position, field.position) &&
                colour == field.colour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(position, colour);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("position", position)
                .add("colour", colour)
                .toString();
    }
}