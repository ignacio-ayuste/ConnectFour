package com.game.connectfour.controller;

import com.game.connectfour.model.Game;
import com.game.connectfour.model.Player;
import com.game.connectfour.service.GameService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Controller
@RequestMapping(value = "game")
public class GameController {

    public static final Logger LOG = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;

    @Autowired
    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @ApiOperation(value = "Create new game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game has been created"),
            @ApiResponse(code = 500, message = "Error occurred - game has not been created")
    })
    @PostMapping( value= "/create")
    @ResponseBody
    public Game create(@ApiParam(name = "player", value = "Player who starts new game", required = true) @RequestBody @NotNull @Valid Player player) {
        LOG.info("Creating new Game with player {}", player);

        return gameService.create(player);
    }

    @PutMapping( value= "/{id}/join")
    @ResponseBody
    @ApiOperation(value = "Join existing game")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Game has been joined"),
            @ApiResponse(code = 404, message = "Game does not exist or has been already completed"),
            @ApiResponse(code = 500, message = "Error occurred - game has not been joined")
    })
    public Game joinGame(@ApiParam(name = "id", value = "ID of the game to join", required = true) @PathVariable("id") UUID id,
                         @ApiParam(name = "player", value = "Player who joins existing game", required = true) @RequestBody @NotNull @Valid Player player) {
        LOG.info("Join Game with Id: {} with player {}", id, player);

        return gameService.joinGame(id,player);
    }

    @PutMapping( value= "/{id}/place/{colour}/column/{column}")
    @ResponseBody
    @ApiOperation(value = "Place a checker into column")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Checker has been placed"),
            @ApiResponse(code = 404, message = "Game does not exist or has been already completed"),
            @ApiResponse(code = 500, message = "Error occurred - Checker has not been placed")
    })
    public Game placeChecker(@ApiParam(name = "id", value = "ID of the game", required = true) @PathVariable("id") UUID id,
                             @ApiParam(name = "colour", value = "Colour of the Checker being dropped", required = true) @PathVariable("colour") Player.Colour colour,
                             @ApiParam(name = "column", value = "Column the Checker being dropped into", required = true) @PathVariable("column") int column) {
        LOG.info("Place checker in Game with Id: {} with colour {} in the column: {}", id, colour, column);

        Game game = gameService.findGameByID(id);
        gameService.placeChecker(game, colour, column);

        return game;
    }

}