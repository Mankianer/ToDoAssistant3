package de.mankianer.todoassistant3.modules.telegram.commands;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.Utils;
import de.mankianer.todoassistant3.models.todo.ToDoStatus;
import de.mankianer.todoassistant3.services.todo.ToDoService;
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
              Utils.TodosToMarkdownMessage(toDoService.getAllToDosByStatus(ToDoStatus.IN_PLANING));
    } else {
      planingCardsWithDueTodayAsMessageWithMarkdown =
          Utils.TodosToMarkdownMessage(toDoService.getAllToDosByStatus(ToDoStatus.IN_PLANING).stream().filter(toDo -> !toDo.getDueDate().isAfter(LocalDateTime.now())).toList());
      if (planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
        planingCardsWithDueTodayAsMessageWithMarkdown =
            "Es müssen heute keine Todos eingeplant werden\\.\nMit /plan all kannst du dir alle ToDos in der Planung ausgeben lassen\\.";
      }
    }

    message.replyAsMarkdown(planingCardsWithDueTodayAsMessageWithMarkdown);
  }
}
