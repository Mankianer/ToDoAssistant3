package de.mankianer.todoassistant3.telegram.commands.dev;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import de.mankianer.todoassistant3.controller.TrelloController;
import org.springframework.stereotype.Component;

@Component
public class DevPlaningCardCheckCommand extends TelegramCommand {

  private final TrelloController trelloController;

  public DevPlaningCardCheckCommand(
      TelegramService telegramService, TrelloController trelloController) {
    super("dev_plan", "Führ Cron Job TrelloController.checkToPlaningList() aus", telegramService);
    this.trelloController = trelloController;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {
    trelloController.checkToPlaningList();
  }
}
