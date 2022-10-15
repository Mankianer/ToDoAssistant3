package de.mankianer.todoassistant3.services.todo;

import de.mankianer.todoassistant3.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.models.todo.ToDo;

import java.util.Optional;

public interface ToDoAdapter {

    Optional<ToDo> loadToDo(String id);
    ToDo saveToDo(ToDo toDo) throws CouldNotCreateException;
    void deleteToDo(String id);

}
