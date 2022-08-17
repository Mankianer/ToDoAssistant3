package de.mankianer.mankianerstelegramspringstarter.commands.models;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramInUpdate extends TelegramIn {

    @Getter
    private Update update;

    public TelegramInUpdate(Update update, TelegramService telegramService) {
    super(telegramService);
        this.update = update;
    }

  public Message getMessage() {
    return update.getMessage();
    }
}
