package de.mankianer.todoassistant3.core.controllers;

import de.mankianer.todoassistant3.core.services.CommunicationService;
import de.mankianer.todoassistant3.core.services.ToDoService;
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
