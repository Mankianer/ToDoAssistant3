package de.mankianer.todoassistant3.modules.telegram.commands.todos;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.core.models.todos.ToDoStatus;
import de.mankianer.todoassistant3.core.services.ToDoService;
import de.mankianer.todoassistant3.core.utils.ToDoUtils;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class GetPlaningCardsOfToday extends TelegramCommand {

  private ToDoService toDoService;

  public GetPlaningCardsOfToday(TelegramService telegramService, ToDoService toDoService) {
    super(
        "plan",
        "Gibt die Liste der spätestens bis heute einzuplanenden Todos aus",
        telegramService);
    this.toDoService = toDoService;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {

    String planingCardsWithDueTodayAsMessageWithMarkdown = "";
    if (args.length > 0 && "all".equals(args[0])) {
      planingCardsWithDueTodayAsMessageWithMarkdown = "*Alle Todos in Planung:*";
      planingCardsWithDueTodayAsMessageWithMarkdown +=
              ToDoUtils.TodosToMarkdownMessage(toDoService.getAllToDosByStatus(ToDoStatus.IN_PLANING));
    } else {
      planingCardsWithDueTodayAsMessageWithMarkdown =
              ToDoUtils.TodosToMarkdownMessage(toDoService.getAllToDosByStatus(ToDoStatus.IN_PLANING).stream().filter(toDo -> !toDo.getDueDate().isAfter(LocalDateTime.now())).toList());
      if (planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
        planingCardsWithDueTodayAsMessageWithMarkdown =
            "Es müssen heute keine Todos eingeplant werden\\.\nMit /plan all kannst du dir alle ToDos in der Planung ausgeben lassen\\.";
      }
    }

    message.replyAsMarkdown(planingCardsWithDueTodayAsMessageWithMarkdown);
  }
}
