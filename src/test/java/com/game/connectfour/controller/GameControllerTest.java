package com.game.connectfour.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.game.connectfour.model.ApiError;
import com.game.connectfour.model.Board;
import com.game.connectfour.model.Game;
import com.game.connectfour.model.Player;
import com.game.connectfour.service.BoardService;
import com.game.connectfour.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GameControllerTest {

    private static final String BASE_URL = "/game";
    private static final String JOIN_GAME_ENDPOINT = BASE_URL + "/%s/join";
    private static final String PLACE_CHECKER_ENDPOINT = BASE_URL + "/%s/place/%s/column/%d";
    private static final String GAME_CREATE_ENDPOINT = BASE_URL + "/create";
    private final Player firstPlayer = new Player("Tom", Player.Colour.Red);
    private final Player secondPlayer = new Player("Ignacio", Player.Colour.Yellow);

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void whenCreateGameIdHasToBeUnique() {
        Game firstGame = createGameRequest(firstPlayer);
        Game secondGame = createGameRequest(secondPlayer);

        assertThat(firstGame.getId())
                .isNotEqualByComparingTo(secondGame.getId())
                .isNotNull();
    }

    @Test
    public void whenCreateGameTheBoardHasToBeProperlySizedAndBlankFields(){
        Game game = createGameRequest(firstPlayer);

        assertThat(game.getBoard().getFields())
                .hasSize(Board.NUMBER_OF_COLUMNS * Board.NUMBER_OF_ROWS)
                .filteredOn(field -> field.getColour() != null)
                .isEmpty();
    }
    @Test
    public void whenCreateGameTheFirstPlayerHasToBeOnPlayerList(){
        Game game = createGameRequest(firstPlayer);

        assertThat(game.getPlayers()).containsOnly(firstPlayer);
    }

    @Test
    public void whenPlaceCheckerUpdatedGameWithCheckerReflectedOnTheBoard() {
        Game existingGame = createGameRequest(firstPlayer);
        Game game = (Game) placeCheckerRequest(existingGame.getId(), firstPlayer.getColour(), 0, Game.class);

        assertThat(game.getId()).isEqualTo(existingGame.getId());
        assertThat(game.getBoard().getFields())
                .filteredOn(field -> field.getPosition().getColumn() == 0 && field.getColour() != null)
                .hasSize(1)
                .first()
                .hasFieldOrPropertyWithValue("position.column", 0)
                .hasFieldOrPropertyWithValue("position.row", 5)
                .hasFieldOrPropertyWithValue("colour", Player.Colour.Red);
    }

    @Test
    public void whenGameDoesNotExistReturn404Response() {
        UUID nonExistentGameId = randomUUID();
        ApiError error = (ApiError) placeCheckerRequest(nonExistentGameId, firstPlayer.getColour(), 0, ApiError.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertEquals(error.getMessage(),String.format(GameService.GAME_WITH_ID_NOT_EXIST, nonExistentGameId));
    }

    @Test
    public void whenDiscIsBeingDroppedOutsideTheBoardReturn500Response() {
        Game existingGame = createGameRequest(firstPlayer);
        int invalidColumn = -1;
        ApiError error = (ApiError) placeCheckerRequest(existingGame.getId(), firstPlayer.getColour(), invalidColumn, ApiError.class);

        assertEquals(HttpStatus.BAD_REQUEST, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertEquals(error.getMessage(), String.format(BoardService.COLUMN_INDEX_DOES_NOT_EXIST_ON_THE_BOARD, invalidColumn));
        assertTrue(error.getErrors().get(0).contains("the request contain invalid arguments"));
    }

    @Test
    public void whenDiscIsBeingDroppedIntoFullColumnReturn500Response() {
        Game existingGame = createGameRequest(firstPlayer);
        for (int i = 0; i < Board.NUMBER_OF_ROWS; i++) {
            placeCheckerRequest(existingGame.getId(), Player.Colour.values()[i % 2], 0, Game.class);
        }

        int fullColumn = 0;
        ApiError error = (ApiError) placeCheckerRequest(existingGame.getId(), firstPlayer.getColour(), fullColumn, ApiError.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertEquals(error.getMessage(), String.format(BoardService.COLUMN_IS_ALREADY_FULL, fullColumn));
        assertTrue(error.getErrors().get(0).contains("error occurred"));
    }

    @Test
    public void whenSecondPlayerJoinTheGameReturnUpdatedGameWithSecondPlayerOnThePlayersList() {
        Game existingGame = createGameRequest(firstPlayer);
        Game game = (Game) joinGameRequest(existingGame.getId(), secondPlayer, Game.class);

        assertThat(game.getId()).isEqualTo(existingGame.getId());
        assertThat(game.getPlayers()).containsOnly(firstPlayer, secondPlayer);
    }

    @Test
    public void whenJoinGameDoesNotExistReturn404Response() {
        UUID nonExistentGameId = randomUUID();
        ApiError error = (ApiError) joinGameRequest(nonExistentGameId, secondPlayer, ApiError.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertEquals(error.getMessage(),String.format(GameService.GAME_WITH_ID_NOT_EXIST, nonExistentGameId));
        assertTrue(error.getErrors().get(0).contains("error occurred"));
    }

    @Test
    public void whenSecondPlayerChoosesTheSameColourReturn500Response() {
        Game existingGame = createGameRequest(firstPlayer);
        ApiError error = (ApiError) joinGameRequest(existingGame.getId(), new Player("Paul", Player.Colour.Red), ApiError.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertEquals(error.getMessage(),Game.TWO_PLAYERS_CANNOT_CHOOSE_THE_SAME_COLOUR);
        assertTrue(error.getErrors().get(0).contains("error occurred"));
    }

    @Test
    public void whenThirdPlayerJoinsReturn500Response() {
        Game existingGame = createGameRequest(firstPlayer);
        joinGameRequest(existingGame.getId(), secondPlayer, Game.class);
        ApiError error = (ApiError) joinGameRequest(existingGame.getId(), new Player("Peter", Player.Colour.Yellow), ApiError.class);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, error.getStatus());
        assertEquals(1, error.getErrors().size());
        assertEquals(error.getMessage(),Game.GAME_CANNOT_HAVE_MORE_THEN_2_PLAYERS);
        assertTrue(error.getErrors().get(0).contains("error occurred"));
    }

    @Test
    public void whenPlayerConnectsFourPiecesGameShouldEnd() {
        Game game = createGameRequest(firstPlayer);
        assertThat(game.getPlayers()).hasSize(1);
        UUID gameId = game.getId();

        game = (Game) joinGameRequest(gameId, secondPlayer, Game.class);
        assertThat(game.getPlayers()).hasSize(2);

        for (int i = 0; i < 4; i++) {
            game = (Game) placeCheckerRequest(gameId, firstPlayer.getColour(), i, Game.class);
            if(game.getOutcome() != null) break;
            game = (Game) placeCheckerRequest(gameId, secondPlayer.getColour(), i, Game.class);
        }

        assertThat(game.getOutcome().getWinner()).isEqualTo(firstPlayer);
    }

    private Game createGameRequest(Player player) {
        Game game = null;
        try{
            MockHttpServletResponse result = mvc.perform(post(GAME_CREATE_ENDPOINT)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(player)))
                    .andExpect(status().isOk())
                    .andReturn().getResponse();

            game = mapper.readValue(result.getContentAsString(), Game.class);
        } catch (Exception e) {
            throw new RuntimeException("Error Creating game Request, Error {}", e);
        }
        return game;
    }

    private Object placeCheckerRequest(UUID id, Player.Colour colour, int column, Class returnClass) {
        Object response = null;
        try{
            MockHttpServletResponse result = mvc.perform(put(String.format(PLACE_CHECKER_ENDPOINT, id, colour, column))
                    .contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andReturn().getResponse();

            response = mapper.readValue(result.getContentAsString(), returnClass);
        } catch (Exception e) {
            throw new RuntimeException("Error Placing checker Request, Error {}", e);
        }
        return response;
    }

    private Object joinGameRequest(UUID gameId, Player player, Class returnClass) {
        Object response = null;
        try{
            MockHttpServletResponse result = mvc.perform(put(String.format(JOIN_GAME_ENDPOINT, gameId))
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(mapper.writeValueAsString(player)))
                    .andReturn().getResponse();

            response = mapper.readValue(result.getContentAsString(), returnClass);
        } catch (Exception e) {
            throw new RuntimeException("Error Creating game Request, Error {}", e);
        }
        return response;
    }

}