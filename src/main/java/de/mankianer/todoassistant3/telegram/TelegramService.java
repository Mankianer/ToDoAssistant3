package de.mankianer.todoassistant3.telegram;

import de.mankianer.todoassistant3.telegram.models.TelegramInMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

@Service
public class TelegramService {

  private final TelegramBot telegramBot;

  private List<Consumer<TelegramInMessage>> messageHandlerFunctions = new ArrayList<>();

  public TelegramService(TelegramBot telegramBot) {
    this.telegramBot = telegramBot;
  }

  @PostConstruct
  public void init() {
    telegramBot.registerUpdateHandlerFunction(this::handleMessage);
  }

  private void handleMessage(Update update) {
    messageHandlerFunctions.forEach(f -> f.accept(new TelegramInMessage(update, this)));
  }

  public void registerMessageHandlerFunction(Consumer<TelegramInMessage> messageHandlerFunction) {
    messageHandlerFunctions.add(messageHandlerFunction);
  }

  public void sendMessage(SendMessage message) {
    telegramBot.sendMessage(message);
  }
}
