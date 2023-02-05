package de.mankianer.todoassistant3.modules.telegram;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.todos.ToDo;
import de.mankianer.todoassistant3.core.services.RoutineService;
import de.mankianer.todoassistant3.core.services.ToDoService;
import de.mankianer.todoassistant3.modules.telegram.handler.TelegramRoutineEventHandler;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class TelegramController {

    private TelegramService telegramService;
    private ToDoService toDoService;
    private RoutineService routineService;

    public TelegramController(TelegramService telegramService, ToDoService toDoService, RoutineService routineService) {
        this.telegramService = telegramService;
        this.toDoService = toDoService;
        this.routineService = routineService;
    }

    @PostConstruct
    public void init() {
        telegramService.setMessageHandlerFunction(this::handleIncomingMessage);
        routineService.setRoutineUpdateToScheduleListener(TelegramRoutineEventHandler.handleRoutinesUpdateToScheduleEvent(telegramService, routineService));
    }

    public void handleIncomingMessage(TelegramInUpdate update) {
        String text = update.getUpdate().getMessage().getText();
        try {
            ToDo toDo = toDoService.createToDo(text, null);
            update.reply("ToDo wurde erstellt: " + toDo.getUrl());
        } catch (CouldNotCreateException e) {
            update.reply("ToDo konnte nicht erstellt werden: \n" + e.getMessage());
        }
    }

}
