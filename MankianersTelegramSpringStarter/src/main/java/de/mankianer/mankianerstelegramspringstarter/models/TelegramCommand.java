package de.mankianer.mankianerstelegramspringstarter.models;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import lombok.Getter;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;

@Getter
public abstract class TelegramCommand extends BotCommand implements TelegramCommandInterface {

    private TelegramService telegramService;

    /**
     * Construct a command
     *
     * @param commandIdentifier the unique identifier of this command (e.g. the command string to
     *                          enter into chat)
     * @param description       the description of this command
     */
    public TelegramCommand(String commandIdentifier, String description, TelegramService telegramService) {
        super(commandIdentifier, description);
        this.telegramService = telegramService;
    }

    public void processMessage(AbsSender absSender, Message message, String[] arguments) {
        if(!this.telegramService.handleUserValidation(message)) return;
        onExecute(new TelegramInMessage(message, telegramService), arguments);
    }

    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        throw new UnsupportedOperationException("Not implemented");
    }

}
