package de.mankianer.todoassistant3.controller;

import com.julienvey.trello.domain.Card;
import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import de.mankianer.todoassistant3.services.TrelloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Log4j2
@Component
public class TrelloController {

  private final TrelloService trelloService;
  private final TelegramService telegramService;

  public TrelloController(TrelloService trelloService, TelegramService telegramService) {
    this.trelloService = trelloService;
    this.telegramService = telegramService;
    telegramService.setMessageHandlerFunction(this::handleIncomingMessage);
  }

  @PostConstruct
  public void init() {
    if (trelloService.loadTrelloData()) {
      telegramService.broadcastMessage("Trello data loaded successfully");
    } else {
      telegramService.broadcastMessage("Trello data loaded failed!");
    }
  }

  public void handleIncomingMessage(TelegramInUpdate update) {
    String text = update.getUpdate().getMessage().getText();
    Card card = new Card();
    card.setName(text);
    card.setDue(getDueDate());
    card = trelloService.getPlaningList().createCard(card);

    card.addLabels("ToDoAssistant");
    update.reply("Karte wurde erstellt: " + card.getUrl());
  }

  private Date getDueDate() {
    return Date.from(
        LocalDate.now().plusDays(2).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
  }
}
