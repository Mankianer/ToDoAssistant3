package de.mankianer.todoassistant3.modules.telegram.commands;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.Utils;
import de.mankianer.todoassistant3.modules.trello.TrelloService;
import org.springframework.stereotype.Component;

@Component
public class GetPlaningCardsOfToday extends TelegramCommand {

  private final TrelloService trelloService;

  public GetPlaningCardsOfToday(TelegramService telegramService, TrelloService trelloService) {
    super(
        "plan",
        "Gibt die Liste der spätestens bis heute einzuplanenden Todos aus",
        telegramService);
    this.trelloService = trelloService;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {

    String planingCardsWithDueTodayAsMessageWithMarkdown = "";
    if (args.length > 0 && "all".equals(args[0])) {
      planingCardsWithDueTodayAsMessageWithMarkdown = "*Alle Todos in Planung:*";
      planingCardsWithDueTodayAsMessageWithMarkdown +=
          Utils.CardsToMarkdownMessage(trelloService.getCards(trelloService.getPlaningList()));
    } else {
      planingCardsWithDueTodayAsMessageWithMarkdown =
          trelloService.getPlaningCardsWithDueTodayAsMessageWithMarkdown();
      if (planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
        planingCardsWithDueTodayAsMessageWithMarkdown =
            "Es müssen heute keine Todos eingeplant werden\\.\nMit /plan all kannst du dir alle ToDos in der Planung ausgeben lassen\\.";
      }
    }

    message.replyAsMarkdown(planingCardsWithDueTodayAsMessageWithMarkdown);
  }
}
