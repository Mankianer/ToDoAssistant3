package de.mankianer.mankianerstelegramspringstarter.commands;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Component
public class RegisterCommand extends TelegramCommand {

  private String unregisterCommandIdentifier;

  /**
   * Construct a command
   *
   * @param commandIdentifier the unique identifier of this command (e.g. the command string to
   *     enter into chat)
   * @param telegramService
   */
  public RegisterCommand(
      @Value("${telegram.command.register:start}") String commandIdentifier,
      @Value("${telegram.command.unregister:stop}") String unregisterCommandIdentifier,
      TelegramService telegramService) {
    super(commandIdentifier, "Registriert dich.", telegramService);
    this.unregisterCommandIdentifier = unregisterCommandIdentifier;
  }

  public void processMessage(AbsSender absSender, Message message, String[] arguments) {
    onExecute(new TelegramInMessage(message, getTelegramService()), arguments);
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {
    if (!getTelegramService().isUserRegistered(message.getMessage().getFrom())) {
      getTelegramService().registerUser(message.getMessage());
      message.reply(
          "Hallo "
              + message.getMessage().getFrom().getFirstName()
              + "! \nDu bist nun Registriert! \nUm dich wieder abzumelden gebe /"
              + unregisterCommandIdentifier
              + " ein.");
    } else {
      message.reply("Du bist bereits registriert!");
    }
  }
}
