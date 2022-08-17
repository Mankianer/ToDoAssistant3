package de.mankianer.todoassistant3.telegram.commands;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.services.TrelloService;
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
    String[] messageText = new String[] {"Das ist der Plan für heute:"};
    trelloService
        .getPlaningCardsWithDueToday()
        .forEach(
            card ->
                messageText[0] +=
                    String.format("\n  \\*__%s__: *%s* ", card.getId(), card.getName()));

    message.replyAsMarkdown(messageText[0]);
  }
}
