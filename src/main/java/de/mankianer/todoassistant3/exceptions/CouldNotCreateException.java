package de.mankianer.todoassistant3.exceptions;

import de.mankianer.todoassistant3.models.todo.ToDo;

public class CouldNotCreateException extends Exception {
    public CouldNotCreateException(ToDo todo, Exception e) {
        super("Could Not Create todo: " + todo, e);
    }
}
