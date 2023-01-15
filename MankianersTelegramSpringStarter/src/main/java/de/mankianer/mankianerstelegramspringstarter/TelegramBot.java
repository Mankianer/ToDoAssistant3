package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversationInterface;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.function.Consumer;

@Log4j2
@Component
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramBot extends TelegramLongPollingCommandBot {

  private final TelegramProperties telegramProperties;
  private final UserHandler fileUserHandler;

  private final TelegramConversationController telegramConversationController;
  private Consumer<Update> invalidCommandHandlerFunction =
      update -> {
        User user = update.getMessage().getFrom();
        String message = String.format("Invalid command. Type /help to see available commands.");
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(user.getId());
        sendMessage.setText(message);
        try {
          execute(sendMessage);
        } catch (TelegramApiException e) {
          log.error("Error sending message", e);
        }
      };
  @Setter private Consumer<Update> messageHandlerFuction = invalidCommandHandlerFunction;

  public TelegramBot(TelegramProperties telegramProperties, FileUserHandler fileUserHandler) {
    this.telegramProperties = telegramProperties;
    this.fileUserHandler = fileUserHandler;
    this.telegramConversationController = new TelegramConversationController(this);
  }

  /**
   * Sends a message to the user if Update was handled.
   *
   * @param message
   * @return true if Update is not handled, false if Update is handled.
   */
  public boolean handleUserValidation(Message message) {
    User user = message.getFrom();
    if (!isUserAllowed(user)) {
      SendMessage sendMessage = new SendMessage();
      sendMessage.setChatId(message.getChatId());
      sendMessage.setText("Ich soll nicht mit Fremden reden! \nUnd dich kenn ich nicht!");
      sendMessage(sendMessage);
      return false;
    }
    if (!isUserRegistered(user)) {
      SendMessage sendMessage = new SendMessage();
      sendMessage.setChatId(message.getChatId());
      sendMessage.setText(
          "Hallo "
              + user.getFirstName()
              + "! \nIch bin Mankianers ToDoAssistetBot, bitte registriere dich mit /start");
      sendMessage(sendMessage);
      return false;
    }
    return true;
  }

  @Override
  public void onUpdatesReceived(List<Update> updates) {
    if(telegramConversationController.isConversationActive()) {
      boolean isCommand = updates.stream().filter(update -> update.hasMessage()).anyMatch(update -> update.getMessage().isCommand());
      if (isCommand) {
        telegramConversationController.abortCurrentConversation(TelegramConversationInterface.AbortReason.USER_ABORT);
      }
    }
    super.onUpdatesReceived(updates);
  }

  public void registerUser(Message message) {
    fileUserHandler.registerUser(message);
  }

  public void unregisterUser(User user) {
    fileUserHandler.unregisterUser(user);
  }

  /**
   * checks if user is allowed to send messages
   *
   * @param user
   * @return true if the user is allowed
   */
  public boolean isUserAllowed(User user) {
    return (telegramProperties.getAllowedUsernames().size() == 0)
        || telegramProperties.getAllowedUsernames().contains(user.getUserName());
  }

  public void sendMessage(SendMessage message) {

    try {
      execute(message);
    } catch (TelegramApiException e) {
      log.error("Error while sending message", e);
    }
  }

  public void startConversation(TelegramConversationInterface conversation) {
    telegramConversationController.startConversation(conversation);
  }

  public void broadcastMessage(SendMessage message) {
    if(telegramConversationController.isConversationActive()) {
      telegramConversationController.abortCurrentConversation(TelegramConversationInterface.AbortReason.SYSTEM_ABORT);
    }
    fileUserHandler.forEach(
        (username, chatId) -> {
          message.setChatId(chatId);
          sendMessage(message);
        });
  }

  @Override
  public String getBotUsername() {
    return telegramProperties.getUsername();
  }

  @Override
  public String getBotToken() {
    return telegramProperties.getToken();
  }

  @Override
  public void processNonCommandUpdate(Update update) {
    if (!handleUserValidation(update.getMessage())) return;
    if (telegramConversationController.isConversationActive()) {
      telegramConversationController.onAnswer(update.getMessage().getText());
    } else {
      messageHandlerFuction.accept(update);
    }
  }

  @Override
  public void processInvalidCommandUpdate(Update update) {
    if (!handleUserValidation(update.getMessage())) return;
    invalidCommandHandlerFunction.accept(update);
  }

  public boolean isUserRegistered(User user) {
    return fileUserHandler.isUserRegistered(user);
  }
}
