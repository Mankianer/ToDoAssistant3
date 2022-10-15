package de.mankianer.todoassistant3.controllers;

import de.mankianer.todoassistant3.models.message.Message;
import de.mankianer.todoassistant3.models.todo.ToDo;
import de.mankianer.todoassistant3.services.communication.CommunicationService;
import de.mankianer.todoassistant3.services.todo.ToDoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class ToDoController {

    private final ToDoService toDoService;
    private CommunicationService communicationService;

    public ToDoController(@Autowired ToDoService toDoService, @Autowired CommunicationService communicationService) {
        this.toDoService = toDoService;
        this.communicationService = communicationService;
    }

    public ToDo createToDo(String name, String description, Optional<Message> message) {
        try {
            ToDo toDo = this.toDoService.createToDo(name, description);
            message.ifPresent(m -> communicationService.replyToMessage(m,"ToDo created", true ));
            return toDo;
        } catch (Exception e) {
            log.warn("Could not create ToDo", e);
            message.ifPresent(m -> communicationService.replyToMessage(m,"ToDo konnte nicht erstellt werden!\n" + e.getMessage(), false ));
        }
        return null;
    }
}
