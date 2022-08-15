package de.mankianer.todoassistant3;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.models.TelegramInMessage;
import de.mankianer.mankianerstelegramspringstarter.models.TelegramInUpdate;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
public class HelloTelegramCommand extends TelegramCommand {

  public HelloTelegramCommand(TelegramService telegramService) {
    super("hello", "Hello World", telegramService);
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] arguments) {
    message.reply("Du mich auch hallo: " + Arrays.toString(arguments));
  }

}
