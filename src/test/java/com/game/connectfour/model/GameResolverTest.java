package com.game.connectfour.model;

import com.game.connectfour.service.BoardService;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.game.connectfour.model.GameResolverTest.CheckerMove.redIntoColumn;
import static com.game.connectfour.model.GameResolverTest.CheckerMove.yellowIntoColumn;
import static com.google.common.collect.Lists.newArrayList;

@RunWith(Parameterized.class)
public class GameResolverTest {

    private static final GameResolver analyser = new GameResolver();

    @Parameterized.Parameter(0)
    public List<CheckerMove> checkerMoves;
    @Parameterized.Parameter(1)
    public Optional<Player.Colour> outcome;

    private BoardService boardService = new BoardService();


    private static Object[][] checkersConnectedInTheSameRow(){
        return new Object[][]{
                {newArrayList(), Optional.empty()},
                {newArrayList(redIntoColumn(0), redIntoColumn(1), redIntoColumn(2), redIntoColumn(3)), Optional.of(Player.Colour.Red)},
                {newArrayList(redIntoColumn(1), redIntoColumn(2), redIntoColumn(3), redIntoColumn(4)), Optional.of(Player.Colour.Red)},
                {newArrayList(redIntoColumn(2), redIntoColumn(3), redIntoColumn(4), redIntoColumn(5)), Optional.of(Player.Colour.Red)},
                {newArrayList(redIntoColumn(3), redIntoColumn(4), redIntoColumn(5), redIntoColumn(6)), Optional.of(Player.Colour.Red)}
                };

    }

    private static Object[][] checkersConnectedInTheSameColumn() {
        return new Object[][]{
                // discs connected in a column
                {newArrayList(
                        redIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0)
                ), Optional.of(Player.Colour.Red)},
                {newArrayList(
                        yellowIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0)
                ), Optional.of(Player.Colour.Red)},
                {newArrayList(
                        yellowIntoColumn(0),
                        yellowIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0),
                        redIntoColumn(0)
                ), Optional.of(Player.Colour.Red)},
            };
    }

    private static Object[][] checkersConnectedDiagonalFromBottomToTop() {
        // discs connected diagonal (bottom - top)
        return new Object[][]{
                {newArrayList(redIntoColumn(0), yellowIntoColumn(1), redIntoColumn(2), yellowIntoColumn(3),
                        redIntoColumn(1), yellowIntoColumn(2), redIntoColumn(3),
                        redIntoColumn(2), yellowIntoColumn(3),
                        redIntoColumn(3)), Optional.of(Player.Colour.Red)}
        };
    }

    private static Object[][] checkersConnectedDiagonalFromTopToBottom() {

        return new Object[][]{
                // discs connected diagonal (top - bottom)
                {newArrayList(
                        yellowIntoColumn(0), redIntoColumn(1), yellowIntoColumn(2), redIntoColumn(3),
                        redIntoColumn(0), yellowIntoColumn(1), redIntoColumn(2),
                        yellowIntoColumn(0), redIntoColumn(1),
                        redIntoColumn(0)
                ), Optional.of(Player.Colour.Red)}
        };
    }

    @Parameterized.Parameters(name = "placing {0} should result in {1}")
    public static Collection<Object[]> scenarios() {

        Object[][] movements = combine(checkersConnectedInTheSameRow(),
                checkersConnectedInTheSameColumn(),
                checkersConnectedDiagonalFromBottomToTop(),
                checkersConnectedDiagonalFromTopToBottom());

        return Arrays.asList(movements);
    }

    public static Object[][] combine(Object[][]... arrays){
        int totalLength = 0;
        for(Object[][] array : arrays){
            totalLength += array.length;
        }
        Object[][] result = new Object[totalLength][totalLength];

        int auxLength = 0;
        for(Object[][] array : arrays){
            System.arraycopy(array, 0, result, auxLength, array.length);
            auxLength += array.length;
        }

        return result;
    }


    @Test
    public void winningColourShouldMatchExpectation() {
        Board board = new Board();
        for (CheckerMove checker : checkerMoves) {
            boardService.placeChecker(board, checker.colour, checker.column);
        }

        Assertions.assertThat(analyser.determineOutcome(board))
                .isEqualTo(outcome);
    }

    static class CheckerMove {
        private final Player.Colour colour;
        private final int column;

        private CheckerMove(Player.Colour colour, int column) {
            this.colour = colour;
            this.column = column;
        }

        static CheckerMove redIntoColumn(int column) {
            return new CheckerMove(Player.Colour.Red, column);
        }

        static CheckerMove yellowIntoColumn(int column) {
            return new CheckerMove(Player.Colour.Yellow, column);
        }

        @Override
        public String toString() {
            return String.format("%s checker into column %d", colour, column).toLowerCase();
        }
    }

}