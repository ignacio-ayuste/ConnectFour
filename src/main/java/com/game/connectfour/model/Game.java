package com.game.connectfour.model;

import com.google.common.base.MoreObjects;
import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;
import static java.util.UUID.randomUUID;

@Document(collection = "Game")
public class Game implements Serializable {

    public static final String GAME_CANNOT_HAVE_MORE_THEN_2_PLAYERS = "Game cannot have more then 2 players";
    public static final String TWO_PLAYERS_CANNOT_CHOOSE_THE_SAME_COLOUR = "Two players cannot choose the same colour";

    @ApiModelProperty(value = "Unique ID of the game", required = true)
    @Id
    private final UUID id;
    @ApiModelProperty(value = "Board used in the game", required = true)
    private final Board board;
    @ApiModelProperty(value = "List of players in the game", required = true)
    private List<Player> players;
    @ApiModelProperty(value = "Outcome of the game")
    private Outcome outcome;

    public Game(Player... players) {
        this(randomUUID(), new Board(), new ArrayList<>());

        for (Player player : players) {
            addPlayer(player);
        }
    }

    @PersistenceConstructor
    public Game(UUID id, Board board, List<Player> players) {
        this.id = id;
        this.board = board;
        this.players = players;
    }

    public UUID getId() {
        return id;
    }

    public Board getBoard() {
        return board;
    }

    public List<Player> getPlayers() {
        return this.players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Outcome getOutcome() {
        return outcome;
    }

    public void setOutcome(Outcome outcome) {
        this.outcome = outcome;
    }

    public void addPlayer(Player player) {
        synchronized (players) {
            checkState(players.size() < 2, GAME_CANNOT_HAVE_MORE_THEN_2_PLAYERS);
            checkState(players.stream().noneMatch(p -> p.getColour().equals(player.getColour())), TWO_PLAYERS_CANNOT_CHOOSE_THE_SAME_COLOUR);

            players.add(player);
        }
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Game game = (Game) o;
        return Objects.equals(id, game.id) &&
                Objects.equals(board, game.board) &&
                Objects.equals(players, game.players) &&
                Objects.equals(outcome, game.outcome);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, board, players, outcome);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .add("board", board)
                .add("players", players)
                .add("outcome", outcome)
                .toString();
    }
}