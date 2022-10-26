package de.mankianer.todoassistant3.core.services.todo;

import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.todo.ToDo;
import de.mankianer.todoassistant3.core.models.todo.ToDoStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Service
public class ToDoService {

    private final ToDoAdapter toDoAdapter;

    public ToDoService(@Autowired ToDoAdapter toDoAdapter) {
        this.toDoAdapter = toDoAdapter;
    }

    public ToDo createToDo(String name, String description) throws CouldNotCreateException {
        ToDo todo = ToDo.builder()
                .name(name)
                .description(description)
                .dueDate(getDueDate())
                .status(ToDoStatus.IN_PLANING)
                .build();
        return this.toDoAdapter.saveToDo(todo);
    }

    private LocalDateTime getDueDate() {
        return LocalDateTime.now().plusDays(2);
    }

    public List<ToDo> getAllToDos() {
        return this.toDoAdapter.loadAllToDos();
    }

    public List<ToDo> getAllToDosByStatus(ToDoStatus status) {
        return getAllToDos().stream().filter(todo -> status.equals(todo.getStatus())).toList();
    }
}
