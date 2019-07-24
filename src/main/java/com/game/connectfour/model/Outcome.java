package com.game.connectfour.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;

import java.util.Objects;

public class Outcome {

    @ApiModelProperty(value = "Player who won the game")
    private final Player winner;

    @JsonCreator
    public  Outcome(Player winner) {
        this.winner = winner;
    }

    public Player getWinner() {
        return winner;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Outcome outcome = (Outcome) o;
        return Objects.equals(winner, outcome.winner);
    }

    @Override
    public int hashCode() {
        return Objects.hash(winner);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("winner", winner)
                .toString();
    }
}