package de.mankianer.todoassistant3.services.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ToDoService {

    private final ToDoAdapter toDoAdapter;

    public ToDoService(@Autowired ToDoAdapter toDoAdapter) {
        this.toDoAdapter = toDoAdapter;
    }
}
