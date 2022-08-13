package de.mankianer.mankianerstelegramspringstarter.models;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class TelegramInMessage {

    @Getter
    private Update update;
    private TelegramService telegramService;

    public TelegramInMessage(Update update, TelegramService telegramService) {
        this.update = update;
        this.telegramService = telegramService;
    }

  public void reply(SendMessage message) {
        message.setReplyToMessageId(update.getMessage().getMessageId());
        message.setChatId(update.getMessage().getChatId());
        telegramService.sendMessage(message);
  }

    public void reply(String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message);
        reply(sendMessage);
    }
}
