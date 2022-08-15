package de.mankianer.mankianerstelegramspringstarter.models;

import org.telegram.telegrambots.extensions.bots.commandbot.commands.IBotCommand;

public interface TelegramCommandInterface extends IBotCommand {

    void onExecute(TelegramInMessage message, String[] args);


}
