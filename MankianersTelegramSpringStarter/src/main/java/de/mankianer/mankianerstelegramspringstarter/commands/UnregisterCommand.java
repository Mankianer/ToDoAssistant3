package de.mankianer.mankianerstelegramspringstarter.commands;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramCommand;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UnregisterCommand extends TelegramCommand {

  private String registerCommandIdentifier;

  /**
   * Construct a command
   *
   * @param commandIdentifier the unique identifier of this command (e.g. the command string to
   *     enter into chat)
   * @param telegramService
   */
  public UnregisterCommand(
      @Value("${telegram.command.unregister:stop}") String commandIdentifier,
      @Value("${telegram.command.register:start}") String registerCommandIdentifier,
      TelegramService telegramService) {
    super(commandIdentifier, "Unregistriert dich.", telegramService);
    this.registerCommandIdentifier = registerCommandIdentifier;
  }

  @Override
  public void onExecute(TelegramInMessage message, String[] args) {
    if (getTelegramService().isUserRegistered(message.getMessage().getFrom())) {
      getTelegramService().unregisterUser(message.getMessage().getFrom());
      message.reply("Du hast dich erfolgreich abgemeldet!");
    } else {
      message.reply(
          "Du bist nicht angemeldet, keine Ahnung wie du das geschafft hast...! \nVersuchs es mal mit /"
              + registerCommandIdentifier
              + ".");
    }
  }
}
