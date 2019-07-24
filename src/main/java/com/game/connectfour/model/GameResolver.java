package com.game.connectfour.model;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class GameResolver {

    public static final int WINNING_NUMBER_OF_DISCS = 4;

    public Optional<Player.Colour> determineOutcome(Board board) {
        if (board.getLastPopulatedField() != null && (areDiscsConnectedInRow(board) || areDiscsConnectedInColumn(board)
                || areDiscsConnectedDiagonalBottomTop(board) || areDiscsConnectedDiagonalTopBottom(board))) {
            return Optional.of(board.getLastPopulatedField().getColour());
        }

        return Optional.empty();
    }

    private boolean areDiscsConnectedInRow(Board board) {
        Position lastPopulatedPosition = board.getLastPopulatedField().getPosition();

        List<Position> positionRange = new ArrayList<>(7);
        for (int column = lastPopulatedPosition.getColumn() - 3; column <= lastPopulatedPosition.getColumn() + 3; column++) {
            positionRange.add(new Position(column, lastPopulatedPosition.getRow()));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> positionRange.contains(field.getPosition()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedInColumn(Board board) {
        Position lastPopulatedPosition = board.getLastPopulatedField().getPosition();

        List<Position> positionRange = new ArrayList<>(7);
        for (int row = lastPopulatedPosition.getRow() - 3; row <= lastPopulatedPosition.getRow() + 3; row++) {
            positionRange.add(new Position(lastPopulatedPosition.getColumn(), row));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> positionRange.contains(field.getPosition()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedDiagonalBottomTop(Board board) {
        Position lastPopulatedPosition = board.getLastPopulatedField().getPosition();

        int bottomRow = lastPopulatedPosition.getRow() + 3;

        List<Position> positionRange = new ArrayList<>(7);
        for (int column = lastPopulatedPosition.getColumn() - 3; column <= lastPopulatedPosition.getColumn() + 3; column++) {
            positionRange.add(new Position(column, bottomRow--));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> positionRange.contains(field.getPosition()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private boolean areDiscsConnectedDiagonalTopBottom(Board board) {
        Position lastPopulatedPosition = board.getLastPopulatedField().getPosition();

        int topRow = lastPopulatedPosition.getRow() - 3;

        List<Position> positionRange = new ArrayList<>(7);
        for (int column = lastPopulatedPosition.getColumn() - 3; column <= lastPopulatedPosition.getColumn() + 3; column++) {
            positionRange.add(new Position(column, topRow++));
        }

        Integer counter = board.getFields().stream()
                .filter(field -> positionRange.contains(field.getPosition()))
                .collect(new FieldCollector(board.getLastPopulatedField().getColour()));

        return counter >= WINNING_NUMBER_OF_DISCS;
    }

    private class FieldCollector implements Collector<Field, LongAdder, Integer> {

        private final Player.Colour colour;

        private FieldCollector(Player.Colour colour) {
            this.colour = colour;
        }

        @Override
        public Supplier<LongAdder> supplier() {
            return LongAdder::new;
        }

        @Override
        public BiConsumer<LongAdder, Field> accumulator() {
            return (counter, field) -> {
                if (field.getColour() == this.colour) {
                    counter.increment();
                } else if (counter.intValue() < WINNING_NUMBER_OF_DISCS) {
                    counter.reset();
                }
            };
        }

        @Override
        public BinaryOperator<LongAdder> combiner() {
            return (left, right) -> {
                left.add(right.longValue());
                return left;
            };
        }

        @Override
        public Function<LongAdder, Integer> finisher() {
            return LongAdder::intValue;
        }

        @Override
        public Set<Characteristics> characteristics() {
            return Collections.emptySet();
        }
    }

}