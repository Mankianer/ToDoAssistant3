package de.mankianer.todoassistant3.core.exceptions;


public class CouldNotCreateException extends Exception {
    public CouldNotCreateException(Object object, Exception e) {
        super("Could Not Create " + object.getClass().getName() + ": " + object, e);
    }
}
