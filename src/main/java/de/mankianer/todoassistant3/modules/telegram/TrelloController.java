package de.mankianer.todoassistant3.modules.telegram;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.todoassistant3.modules.trello.TrelloService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

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

  @Scheduled(cron = "${todo.checkToPlaning.cron.expression}")
  public void checkToPlaningList() {
    String planingCardsWithDueTodayAsMessageWithMarkdown =
        trelloService.getPlaningCardsWithDueTodayAsMessageWithMarkdown();
    if (!planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
      telegramService.broadcastMessageAsMarkdown(planingCardsWithDueTodayAsMessageWithMarkdown);
    }
  }
}
