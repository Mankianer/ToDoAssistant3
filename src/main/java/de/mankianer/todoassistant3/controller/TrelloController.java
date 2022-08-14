package de.mankianer.todoassistant3.controller;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.todoassistant3.services.TrelloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.PostConstruct;

@Log4j2
@Component
public class TrelloController {

  private final TrelloService trelloService;
  private final TelegramService telegramService;

  public TrelloController(TrelloService trelloService, TelegramService telegramService) {
    this.trelloService = trelloService;
    this.telegramService = telegramService;
  }

  @PostConstruct
  public void init() {
    if (trelloService.loadTrelloData()) {
      telegramService.broadcastMessage("Trello data loaded successfully");
    } else {
      telegramService.broadcastMessage("Trello data loaded failed!");
    }
  }
}
