package de.mankianer.todoassistant3.controllers;

import de.mankianer.todoassistant3.services.communication.CommunicationService;
import de.mankianer.todoassistant3.services.todo.ToDoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class ToDoController {

    private final ToDoService toDoService;
    private CommunicationService communicationService;

    public ToDoController(@Autowired ToDoService toDoService, @Autowired CommunicationService communicationService) {
        this.toDoService = toDoService;
        this.communicationService = communicationService;
    }

}
