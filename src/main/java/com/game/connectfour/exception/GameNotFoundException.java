package com.game.connectfour.exception;

public class GameNotFoundException extends RuntimeException{

    public GameNotFoundException(String message) {
        super(message);
    }
}