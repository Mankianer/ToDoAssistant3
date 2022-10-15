package de.mankianer.todoassistant3.services.todo;

import de.mankianer.todoassistant3.model.todo.ToDo;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class TrelloAdapter implements ToDoAdapter {

    @Override
    public Optional<ToDo> loadToDo(String id) {
        return Optional.empty();
    }

    @Override
    public Optional<ToDo> saveToDo(ToDo toDo) throws Exception {
        return Optional.empty();
    }

    @Override
    public Optional<ToDo> deleteToDo(String id) {
        return Optional.empty();
    }
}
