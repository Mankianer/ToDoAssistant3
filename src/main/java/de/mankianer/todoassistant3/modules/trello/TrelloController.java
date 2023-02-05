package de.mankianer.todoassistant3.modules.trello;

import de.mankianer.todoassistant3.core.models.communication.Message;
import de.mankianer.todoassistant3.core.services.CommunicationService;
import de.mankianer.todoassistant3.modules.trello.routines.TrelloRoutineService;
import de.mankianer.todoassistant3.modules.trello.todo.TrelloToDoService;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Log4j2
@Component
public class TrelloController {

  private final TrelloToDoService trelloToDoService;
  private final TrelloRoutineService trelloRoutineService;
  private final CommunicationService communicationService;

  public TrelloController(TrelloToDoService trelloToDoService, TrelloRoutineService trelloRoutineService,
                          CommunicationService communicationService) {
    this.trelloToDoService = trelloToDoService;
    this.trelloRoutineService = trelloRoutineService;
    this.communicationService = communicationService;
  }

  @PostConstruct
  public void init() {
    if (trelloToDoService.loadTrelloData()) {
      communicationService.sendMessage(Message.of("Trello ToDo data loaded successfully"));
    } else {
      communicationService.sendMessage(Message.of("Trello ToDo data loaded failed!"));
    }
    if (trelloRoutineService.loadTrelloData()) {
      communicationService.sendMessage(Message.of("Trello Routine data loaded successfully"));
    } else {
      communicationService.sendMessage(Message.of("Trello Routine data loaded failed!"));
    }
  }

  @Scheduled(cron = "${todo.checkToPlaning.cron.expression}")
  public void checkToPlaningList() {
    String planingCardsWithDueTodayAsMessageWithMarkdown =
        trelloToDoService.getPlaningCardsWithDueTodayAsMessageWithMarkdown();
    if (!planingCardsWithDueTodayAsMessageWithMarkdown.isBlank()) {
      communicationService.sendMessage(Message.ofMd(planingCardsWithDueTodayAsMessageWithMarkdown));
    }
  }
}
