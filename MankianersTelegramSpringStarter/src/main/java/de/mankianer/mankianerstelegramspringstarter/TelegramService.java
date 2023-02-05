package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommandInterface;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversationInterface;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.helpCommand.HelpCommand;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.PostConstruct;
import java.util.function.Consumer;

@Log4j2
@Service
public class TelegramService {

  private final TelegramBot telegramBot;

  public TelegramService(TelegramBot telegramBot) {
    this.telegramBot = telegramBot;
  }

  @PostConstruct
  public void init() {
    telegramBot.register(new HelpCommand());
  }

  public void sendMessage(SendMessage message) {
    telegramBot.sendMessage(message);
  }

  public void broadcastMessage(String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(message);
    broadcastMessage(sendMessage);
  }

  public void broadcastMessageAsMarkdown(String message) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.enableMarkdownV2(true);
    sendMessage.setText(message);
    broadcastMessage(sendMessage);
  }

  public void startConversation(TelegramConversationInterface conversation) {
    telegramBot.startConversation(conversation);
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

  public boolean isUserRegistered(User user) {
    return telegramBot.isUserRegistered(user);
  }

  public void registerUser(Message message) {
    telegramBot.registerUser(message);
  }

  public void unregisterUser(User user) {
    telegramBot.unregisterUser(user);
  }

  public void setMessageHandlerFunction(Consumer<TelegramInUpdate> messageHandlerFuction) {
    telegramBot.setMessageHandlerFuction(
        update -> messageHandlerFuction.accept(new TelegramInUpdate(update, this)));
  }
}
