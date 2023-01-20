package de.mankianer.todoassistant3.modules.telegram.commands.routines;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import de.mankianer.todoassistant3.core.services.routines.RoutineService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Log4j2
@Component
public class GetPlanedRoutines extends TelegramCommand {

  private final RoutineService routineService;

  public GetPlanedRoutines(TelegramService telegramService, RoutineService routineService) {
    super(
        "routine_plan",
        "Gibt die Routinen aus, die in den nächsten 7 Tagen eingeplant werden sollen. \nTage können auch per Parameter angegeben werden. (/routine_plan 14)",
        telegramService);
    this.routineService = routineService;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {
    int days = 7;
    try {
      days = Integer.parseInt(args[0]);
    } catch (Exception e) {
      log.warn("Could not parse days from args[0] : {}", ((args.length > 1) ? args[0] : "null"));
    }

    final int dayL = days;

    var routines = routineService.getAllRoutinesByStatus(RoutineStatus.PLANNED).filter(routine -> !routine.getNextExecution().isAfter(LocalDateTime.now().plusDays(dayL))).toList();

    String messageText = """
            ```
            | NR | Name                 | Datum       |
            |----|----------------------|-------------|
            %s
            ```
            """.formatted(getPlanedRoutineTable(routines));
    message.replyAsMarkdown(messageText);
  }

  private String getPlanedRoutineTable(List<Routine> routines) {
    StringBuilder table = new StringBuilder();
    for (int i = 0; i < routines.size(); i++) {
      table.append(getPlanedRoutineTableRow(i, routines.get(i)));
    }
    return table.toString();
  }

  private String getPlanedRoutineTableRow(int pos, Routine routine) {
    return "| %2d | %-20s | %3$td.%3$tm - %3$ta |\n".formatted(pos, routine.getName(), routine.getNextExecution());
  }
}