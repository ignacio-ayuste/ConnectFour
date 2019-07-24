package com.game.connectfour.model;

import com.game.connectfour.service.GameService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GameTest {

    @Autowired
    private GameService gameService;

    @Test
    public void whenSecondPlayerHasChosenTheSameColourAsFirstPlayerThrowAnException(){
        Game game = new Game();
        game.addPlayer(new Player("Milton", Player.Colour.Red));

        assertThatThrownBy(() -> game.addPlayer(new Player("Homer", Player.Colour.Red)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(Game.TWO_PLAYERS_CANNOT_CHOOSE_THE_SAME_COLOUR);
    }

    @Test
    public void whenThirdPlayerIsBeingAddedThrowAnException(){
        Game game = new Game();
        game.addPlayer(new Player("Juan", Player.Colour.Red));
        game.addPlayer(new Player("Peter", Player.Colour.Yellow));

        assertThatThrownBy(() -> game.addPlayer(new Player("Jennifer", Player.Colour.Yellow)))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(Game.GAME_CANNOT_HAVE_MORE_THEN_2_PLAYERS);
    }

    @Test
    public void whenTheSamePlayerIsMakingTwoConsecutiveMovesThrowAnException() throws Exception {
        Player player = new Player("Ignacio", Player.Colour.Red);
        Game game = new Game(player);
        gameService.placeChecker(game, player.getColour(), 0);

        assertThatThrownBy(() -> gameService.placeChecker(game, player.getColour(), 0))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(GameService.SINGLE_PLAYER_CANNOT_DROP_TWO_CHECKERS_IN_A_ROW);
    }

    @Test
    public void whenGameIsEndAndPlayerContinueDroppingDiscsThrowAnException() throws Exception {
        Player firstPlayer = new Player("Peter", Player.Colour.Red);
        Player secondPlayer = new Player("Paul", Player.Colour.Yellow);
        Game game = new Game(firstPlayer, secondPlayer);

        playGameToTheEnd(game, firstPlayer, secondPlayer);

        assertThatThrownBy(() -> gameService.placeChecker(game, secondPlayer.getColour(), 3))
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage(GameService.GAME_HAS_ALREADY_ENDED);
    }

    @Test
    public void whenFourDiscsHasBeenConnectedShouldHaveOutcome() {
        Player firstPlayer = new Player("John", Player.Colour.Red);
        Player secondPlayer = new Player("Carl", Player.Colour.Yellow);
        Game game = new Game(firstPlayer, secondPlayer);

        playGameToTheEnd(game, firstPlayer, secondPlayer);

        assertThat(game.getOutcome())
                .isEqualTo(new Outcome(firstPlayer));
    }

    private void playGameToTheEnd (Game game, Player firstPlayer, Player secondPlayer){
        for (int i = 0; i < 4; i++) {
            gameService.placeChecker(game, firstPlayer.getColour(), i);
            if(game.getOutcome() != null) break;
            gameService.placeChecker(game, secondPlayer.getColour(), i);
        }
    }

}