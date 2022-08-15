package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommandInterface;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.List;

@Log4j2
@Configuration
public class TelegramCommandStarterConfig {

  @Bean
  @ConditionalOnMissingBean
  public FileUserHandler fileUserHandler() {
    return new FileUserHandler();
  }

  @Bean
  @ConditionalOnMissingBean
  public TelegramProperties telegramProperties() {
    return new TelegramProperties();
  }

  @Bean
  @ConditionalOnMissingBean
  public TelegramBot telegramService(
      TelegramProperties telegramProperties, FileUserHandler fileUserHandler) {
    return new TelegramBot(telegramProperties, fileUserHandler);
  }

  @Bean
  @ConditionalOnMissingBean
  public TelegramService telegramService(TelegramBot telegramBot) {
    return new TelegramService(telegramBot);
  }

  @Bean
  @ConditionalOnMissingBean
  public TelegramCommandInitializer telegramCommandInitializer(
      TelegramService telegramService, ObjectProvider<List<TelegramCommandInterface>> commands) {
    return new TelegramCommandInitializer(
        telegramService, commands.getIfAvailable(Collections::emptyList));
  }
}
