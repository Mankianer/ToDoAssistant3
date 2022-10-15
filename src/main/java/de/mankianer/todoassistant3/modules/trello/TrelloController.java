package de.mankianer.todoassistant3.modules.trello;

import de.mankianer.todoassistant3.models.message.Message;
import de.mankianer.todoassistant3.services.communication.CommunicationService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
public class TrelloController {

  private final TrelloService trelloService;
  private final CommunicationService communicationService;

  public TrelloController(TrelloService trelloService, CommunicationService communicationService) {
    this.trelloService = trelloService;
    this.communicationService = communicationService;
  }

  @PostConstruct
  public void init() {
    if (trelloService.loadTrelloData()) {
      communicationService.sendMessage(Message.of("Trello data loaded successfully"));
    } else {
      communicationService.sendMessage(Message.of("Trello data loaded failed!"));
    }
  }

  @Scheduled(cron = "${todo.checkToPlaning.cron.expression}")
  public void checkToPlaningList() {
    String planingCardsWithDueTodayAsMessageWithMarkdown =
        trelloService.getPlaningCardsWithDueTodayAsMessageWithMarkdown();
    if (!planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
      communicationService.sendMessage(Message.ofMd(planingCardsWithDueTodayAsMessageWithMarkdown));
    }
  }
}
