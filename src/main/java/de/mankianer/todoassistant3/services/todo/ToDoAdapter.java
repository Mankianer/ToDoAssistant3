package de.mankianer.todoassistant3.services.todo;

import de.mankianer.todoassistant3.model.todo.ToDo;

import java.util.Optional;

public interface ToDoAdapter {

    Optional<ToDo> loadToDo(String id);
    Optional<ToDo> saveToDo(ToDo toDo) throws Exception;
    void deleteToDo(String id);

}
