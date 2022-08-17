package de.mankianer.mankianerstelegramspringstarter.commands.models;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramInMessage extends TelegramIn {

    @Getter
    private Message message;

    public TelegramInMessage(Message message, TelegramService telegramService) {
    super(telegramService);
        this.message = message;
  }
}
