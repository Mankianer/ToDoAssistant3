package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.models.TelegramInMessage;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

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

  public void broadcastMessage(String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(message);
    broadcastMessage(sendMessage);
  }

  public void broadcastMessage(SendMessage message) {
    telegramBot.broadcastMessage(message);
  }
}
