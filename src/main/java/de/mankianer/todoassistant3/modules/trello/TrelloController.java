package de.mankianer.todoassistant3.modules.trello;

import de.mankianer.todoassistant3.core.models.communication.Message;
import de.mankianer.todoassistant3.core.services.CommunicationService;
import de.mankianer.todoassistant3.modules.trello.adapter.TrelloRoutineAdapter;
import de.mankianer.todoassistant3.modules.trello.adapter.TrelloToDoAdapter;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
public class TrelloController {

  private final TrelloToDoAdapter trelloToDoAdapter;
  private final TrelloRoutineAdapter trelloRoutineAdapter;
  private final CommunicationService communicationService;

  public TrelloController(TrelloToDoAdapter trelloToDoAdapter, TrelloRoutineAdapter trelloRoutineAdapter,
                          CommunicationService communicationService) {
    this.trelloToDoAdapter = trelloToDoAdapter;
    this.trelloRoutineAdapter = trelloRoutineAdapter;
    this.communicationService = communicationService;
  }

  @PostConstruct
  public void init() {
    if (trelloToDoAdapter.loadTrelloData()) {
      communicationService.sendMessage(Message.of("Trello ToDo data loaded successfully"));
    } else {
      communicationService.sendMessage(Message.of("Trello ToDo data loaded failed!"));
    }
    if (trelloRoutineAdapter.loadTrelloData()) {
      communicationService.sendMessage(Message.of("Trello Routine data loaded successfully"));
    } else {
      communicationService.sendMessage(Message.of("Trello Routine data loaded failed!"));
    }
  }

  @Scheduled(cron = "${todo.checkToPlaning.cron.expression}")
  public void checkToPlaningList() {
    String planingCardsWithDueTodayAsMessageWithMarkdown =
        trelloToDoAdapter.getPlaningCardsWithDueTodayAsMessageWithMarkdown();
    if (!planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
      communicationService.sendMessage(Message.ofMd(planingCardsWithDueTodayAsMessageWithMarkdown));
    }
  }
}
