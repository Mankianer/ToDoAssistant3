package de.mankianer.todoassistant3.services.todo;

import de.mankianer.todoassistant3.models.todo.ToDo;
import de.mankianer.todoassistant3.models.todo.ToDoStatus;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
public class ToDoService {

    private final ToDoAdapter toDoAdapter;

    public ToDoService(@Autowired ToDoAdapter toDoAdapter) {
        this.toDoAdapter = toDoAdapter;
    }

    public ToDo createToDo(String name, String description) throws Exception {
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
}
