package com.game.connectfour.model;

import com.game.connectfour.service.BoardService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BoardTest {

    @Autowired
    private BoardService boardService;

    @Test
    public void whenPlaceCheckerMustOccupyNextAvailableFieldInTheColumn(){
        Board board = new Board();

        boardService.placeChecker(board, Player.Colour.Red, 0);
        assertThat(board.getFields())
                .filteredOn(field -> field.getPosition().getColumn() == 0 && field.getColour() != null)
                .containsOnly(new Field(new Position(0, 5), Player.Colour.Red));

        boardService.placeChecker(board, Player.Colour.Yellow, 0);
        assertThat(board.getFields())
                .filteredOn(field -> field.getPosition().getColumn() == 0 && field.getColour() != null)
                .containsOnly(new Field(new Position(0, 4), Player.Colour.Yellow), new Field(new Position(0, 5), Player.Colour.Red));
    }

    @Test
    public void whenColourOfTheCheckerIsNullThrowException(){
        Board board = new Board();

        Assertions.assertThatThrownBy(() -> boardService.placeChecker(board,null, 0))
                .isInstanceOf(NullPointerException.class)
                .hasMessage(BoardService.COLOUR_OF_THE_CHECKER_CANNOT_BE_NULL);
    }

    @Test
    public void whenTryToPlaceCheckerOutsideTheBoardThrowException(){
        Board board = new Board();

        int invalidColumn = -1;
        Assertions.assertThatThrownBy(() -> boardService.placeChecker(board, Player.Colour.Red, invalidColumn))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(BoardService.COLUMN_INDEX_DOES_NOT_EXIST_ON_THE_BOARD, invalidColumn));

        int column = 7;
        Assertions.assertThatThrownBy(() -> boardService.placeChecker(board, Player.Colour.Red, column))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(String.format(BoardService.COLUMN_INDEX_DOES_NOT_EXIST_ON_THE_BOARD, column));
    }

    @Test
    public void whenColumnIsFullCheckerCannotBePlacedThrowException(){
        Board board = new Board();

        for (int i = 0; i < Board.NUMBER_OF_ROWS; i++) {
            boardService.placeChecker(board, Player.Colour.Red, 0);
        }

        int column = 0;
        Assertions.assertThatThrownBy(() -> boardService.placeChecker(board, Player.Colour.Yellow, column))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage(String.format(BoardService.COLUMN_IS_ALREADY_FULL, column));
    }

}