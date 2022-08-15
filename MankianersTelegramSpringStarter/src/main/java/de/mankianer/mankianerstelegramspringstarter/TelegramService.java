package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.models.TelegramCommandInterface;
import de.mankianer.mankianerstelegramspringstarter.models.TelegramInUpdate;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Log4j2
@Service
public class TelegramService {

  private final TelegramBot telegramBot;

  private List<Function<TelegramInUpdate, Boolean>> messageHandlerFunctions = new ArrayList<>();

  public TelegramService(TelegramBot telegramBot) {
    this.telegramBot = telegramBot;
  }

  @PostConstruct
  public void init() {
    telegramBot.registerUpdateHandlerFunction(this::handleMessage);
  }

  private void handleMessage(Update update) {
    for (Function<TelegramInUpdate, Boolean> function : messageHandlerFunctions) {
      if (function.apply(new TelegramInUpdate(update, this))) {
        return;
      }
    }
  }

  public void registerMessageHandlerFunction(Function<TelegramInUpdate, Boolean> messageHandlerFunction) {
    messageHandlerFunctions.add(messageHandlerFunction);
  }

  public void sendMessage(SendMessage message) {
    telegramBot.sendMessage(message);
  }

  public void broadcastMessage(String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(message);
    broadcastMessage(sendMessage);
  }

  public void broadcastMessage(SendMessage message) {
    telegramBot.broadcastMessage(message);
  }

  /**
   * Sends a message to the user if Update was handled.
   * @param message
   * @return true if Update is not handled, false if Update is handled.
   */
  public boolean handleUserValidation(Message message) {
    return telegramBot.handleUserValidation(message);
  }

  public void registerCommand(TelegramCommandInterface command) {
    telegramBot.register(command);
  }

  public void deregisterCommand(TelegramCommandInterface command) {
    telegramBot.deregister(command);
  }
}
