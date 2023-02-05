package de.mankianer.todoassistant3.core.adapter;

import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.todo.ToDo;

import java.util.List;
import java.util.Optional;

public interface ToDoAdapter {

    Optional<ToDo> loadToDo(String id);
    ToDo saveToDo(ToDo toDo) throws CouldNotCreateException;
    void deleteToDo(String id);

    List<ToDo> loadAllToDos();

}
