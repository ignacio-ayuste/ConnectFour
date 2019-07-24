package com.game.connectfour.service;

import com.game.connectfour.exception.GameNotFoundException;
import com.game.connectfour.model.Game;
import com.game.connectfour.model.GameResolver;
import com.game.connectfour.model.Outcome;
import com.game.connectfour.model.Player;
import com.game.connectfour.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkState;

@Service
public class GameService {

    private static final GameResolver analyser = new GameResolver();
    public static final String SINGLE_PLAYER_CANNOT_DROP_TWO_CHECKERS_IN_A_ROW = "Single player cannot drop two checkers in a row";
    public static final String GAME_HAS_ALREADY_ENDED = "Game has already ended";
    public static final String GAME_WITH_ID_NOT_EXIST = "Game with id: %s doesn't exist";

    private final BoardService boardService;

    private GameRepository gameRepository;

    @Autowired
    public GameService(BoardService boardService, GameRepository gameRepository) {
        this.boardService = boardService;
        this.gameRepository = gameRepository;
    }

    public Game create(Player player){
        Game game = new Game();
        game.addPlayer(player);

        gameRepository.save(game);

        return game;
    }

    public Game findGameByID(UUID gameId) {
        return gameRepository.findById(gameId).orElseThrow(() -> new GameNotFoundException(String.format(GAME_WITH_ID_NOT_EXIST, gameId)));
    }

    public void placeChecker(Game game, Player.Colour colour, int column) {
        checkState(game.getBoard().getLastPopulatedField() == null || game.getBoard().getLastPopulatedField().getColour() != colour, SINGLE_PLAYER_CANNOT_DROP_TWO_CHECKERS_IN_A_ROW);
        checkState(game.getOutcome() == null, GAME_HAS_ALREADY_ENDED);

        boardService.placeChecker(game.getBoard(),colour, column);

        gameRepository.save(game);

        analyser.determineOutcome(game.getBoard()).ifPresent((winningColour) -> {
            Player winner = game.getPlayers().stream()
                    .filter(player -> player.getColour() == winningColour)
                    .findFirst()
                    .get();

            game.setOutcome(new Outcome(winner));
        });
    }

    public Game joinGame(UUID gameId, Player player) {
        Game game = findGameByID(gameId);
        game.addPlayer(player);

        gameRepository.save(game);

        return game;
    }

}