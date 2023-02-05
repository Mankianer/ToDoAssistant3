package de.mankianer.todoassistant3.modules.telegram.commands.routines;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import de.mankianer.todoassistant3.core.services.routines.RoutineService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.List;

@Log4j2
@Component
public class GetScheduledRoutines extends TelegramCommand {

  private final RoutineService routineService;
  public GetScheduledRoutines(TelegramService telegramService, RoutineService routineService) {
    super(
        "routine_scheduled",
        "Gibt die Routinen aus, die Angedacht sind.",
        telegramService);
    this.routineService = routineService;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {
    var routines = routineService.getAllRoutinesByStatus(RoutineStatus.SCHEDULED).toList();
    message.replyAsMarkdown(getMessageText(routines, routineService.getUrlToData()));
  }

  public static String getMessageText(List<Routine> routines, String url) {
    return "[*Heutige Routinen*](" + url + ")\n" + TelegramRoutineUtils.getRoutineTable(routines);
  }
}
