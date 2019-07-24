package com.game.connectfour.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.util.Objects;

import static com.google.common.base.Preconditions.checkNotNull;

public class Player {

    public static final String PLAYER_NAME_CANNOT_BE_NULL = "Player name cannot be null";
    public static final String PLAYER_COLOUR_CANNOT_BE_NULL = "Player colour cannot be null";

    @NotNull(message = PLAYER_NAME_CANNOT_BE_NULL)
    @ApiModelProperty(value = "Name of the player", required = true, example = "Tom")
    private final String name;
    @NotNull(message = PLAYER_COLOUR_CANNOT_BE_NULL)
    @ApiModelProperty(value = "Colour selected by the player", required = true, example = "Red")
    private final Colour colour;

    @JsonCreator
    public Player(String name, Colour colour) {
        checkNotNull(name, PLAYER_NAME_CANNOT_BE_NULL);
        checkNotNull(colour, PLAYER_COLOUR_CANNOT_BE_NULL);

        this.name = name;
        this.colour = colour;
    }

    public String getName() {
        return name;
    }

    public Colour getColour() {
        return colour;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(name, player.name) &&
                colour == player.colour;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, colour);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("name", name)
                .add("colour", colour)
                .toString();
    }

    public enum Colour {
        Red, Yellow
    }
}
