package com.game.connectfour.service;

import com.game.connectfour.model.Board;
import com.game.connectfour.model.Field;
import com.game.connectfour.model.Player;
import org.springframework.stereotype.Service;

import java.util.OptionalInt;

import static com.google.common.base.Preconditions.*;

@Service
public class BoardService {

    public static final String COLUMN_INDEX_DOES_NOT_EXIST_ON_THE_BOARD = "Column %d does not exist on the board";
    public static final String COLOUR_OF_THE_CHECKER_CANNOT_BE_NULL = "Colour of the checker cannot be null";
    public static final String COLUMN_IS_ALREADY_FULL = "Column %d is already full";

    public synchronized Field placeChecker(Board board, Player.Colour colour, int column) {
        checkNotNull(colour, COLOUR_OF_THE_CHECKER_CANNOT_BE_NULL);
        checkArgument(column >= 0 && column < Board.NUMBER_OF_COLUMNS, String.format(COLUMN_INDEX_DOES_NOT_EXIST_ON_THE_BOARD, column));

        OptionalInt lastOccupiedRow = findLastFilledRow(board,column);

        lastOccupiedRow.ifPresent((row) -> checkState(row != 0, String.format(COLUMN_IS_ALREADY_FULL, column)));

        Field availableField = findAvailableField(board, column, lastOccupiedRow);
        availableField.setColour(colour);

        board.setLastPopulatedField(availableField);
        return availableField;
    }

    private OptionalInt findLastFilledRow(Board board, int column) {
        return board.getFields().stream()
                .filter(field -> field.getPosition().getColumn() == column && field.getColour() != null)
                .mapToInt(field -> field.getPosition().getRow())
                .min();
    }

    private Field findAvailableField(Board board, int column, OptionalInt lastOccupiedRow) {
        return board.getFields().stream()
                .filter(field -> field.getPosition().getColumn() == column && field.getPosition().getRow() == lastOccupiedRow.orElse(Board.NUMBER_OF_ROWS) - 1)
                .findFirst()
                .get();
    }

}