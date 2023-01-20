package de.mankianer.todoassistant3.modules.telegram.commands.routines;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import de.mankianer.todoassistant3.core.services.routines.RoutineService;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class GetPlanedRoutines extends TelegramCommand {

  private final RoutineService routineService;

  public GetPlanedRoutines(TelegramService telegramService, RoutineService routineService) {
    super(
        "routine_plan",
        "Gibt die Routinen aus, die in den nächsten 7 Tagen eingeplant werden sollen.",
        telegramService);
    this.routineService = routineService;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {

    // get all routines with status planed for the next 7 days
    // formatiere die Routinen in eine Tabelle
    // sende die Tabelle als Antwort
    var routines = routineService.getAllRoutinesByStatus(RoutineStatus.PLANNED).filter(routine -> !routine.getNextExecution().isAfter(LocalDateTime.now().plusDays(7))).toList();
//    String planedRoutinesAsMessageWithMarkdown = Utils.RoutinesToMarkdownMessage(routineService.getAllRoutinesByStatus(RoutineStatus.PLANNED).stream().filter(routine -> !routine.getNextExecution().isAfter(LocalDateTime.now().plusDays(7))).toList());
//
//    String planingCardsWithDueTodayAsMessageWithMarkdown = "";
//    if (args.length > 0 && "all".equals(args[0])) {
//      planingCardsWithDueTodayAsMessageWithMarkdown = "*Alle dem nächst eingeplanten Routinen:*";
//      planingCardsWithDueTodayAsMessageWithMarkdown += routineService.getAllRoutinesByStatus(RoutineStatus.PLANNED).stream().
//    } else {
//      planingCardsWithDueTodayAsMessageWithMarkdown =
//          Utils.TodosToMarkdownMessage(routineService.getAllToDosByStatus(ToDoStatus.IN_PLANING).stream().filter(toDo -> !toDo.getDueDate().isAfter(LocalDateTime.now())).toList());
//      if (planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
//        planingCardsWithDueTodayAsMessageWithMarkdown =
//            "Es müssen heute keine Todos eingeplant werden\\.\nMit /plan all kannst du dir alle ToDos in der Planung ausgeben lassen\\.";
//      }
//    }
//

    String messageText = """
            ```
            | NR | Name                 | Datum       |
            |----|----------------------|-------------|
            %s
            ```
            """.formatted(getPlanedRoutineTable(routines));
    message.replyAsMarkdown(messageText);

//    TelegramConversation conversation = TelegramConversation.builder("wie geht's?", message.getMessage().getChatId())
//            .on("gut").then("super").finish()
//            .on("ok").then("ok")
//                              .on("und dir?").then("Mir geht es gut!")
//            .finishToMain()
//            .on("schlecht").then("oh nein").finish()
//            .build();
//
//    getTelegramService().startConversation(conversation);
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
