package de.mankianer.todoassistant3.modules.telegram.handler;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.services.RoutineService;
import de.mankianer.todoassistant3.modules.telegram.commands.routines.GetPlanedRoutines;

import java.util.List;
import java.util.function.Consumer;

public class TelegramRoutineEventHandler {

    public static Consumer<List<Routine>> handleRoutinesUpdateToScheduleEvent(TelegramService telegramService, RoutineService routineService) {
        return routines -> telegramService.broadcastMessageAsMarkdown(GetPlanedRoutines.getMessageText(routines, routineService.getUrlToData()));
    }
}
