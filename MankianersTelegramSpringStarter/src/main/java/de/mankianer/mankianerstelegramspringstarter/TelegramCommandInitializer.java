package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommandInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;

import java.util.List;

@Log4j2
public class TelegramCommandInitializer implements InitializingBean {

  private TelegramService telegramService;
  private List<TelegramCommandInterface> commands;

  public TelegramCommandInitializer(
      TelegramService telegramService, List<TelegramCommandInterface> commands) {
    this.telegramService = telegramService;
    this.commands = commands;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    commands.forEach(telegramService::registerCommand);
  }
}
