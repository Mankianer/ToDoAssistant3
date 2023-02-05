package de.mankianer.todoassistant3.modules.telegram;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.todoassistant3.core.services.RoutineService;
import de.mankianer.todoassistant3.core.services.ToDoService;
import de.mankianer.todoassistant3.modules.telegram.handler.TelegramEventHandler;
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
        telegramService.setMessageHandlerFunction(TelegramEventHandler.handleIncomingMessage(toDoService));
        routineService.setRoutineUpdateToScheduleListener(TelegramRoutineEventHandler.handleRoutinesUpdateToScheduleEvent(telegramService, routineService));
    }

}
