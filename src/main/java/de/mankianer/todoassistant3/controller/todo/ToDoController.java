package de.mankianer.todoassistant3.controller.todo;

import de.mankianer.todoassistant3.services.todo.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToDoController {

    private final ToDoService toDoService;

    public ToDoController(@Autowired ToDoService toDoService) {
        this.toDoService = toDoService;
    }
}
