package de.mankianer.mankianerstelegramspringstarter.commands.models;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import lombok.Getter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramInMessage {

    @Getter
    private Message message;
    private TelegramService telegramService;

    public TelegramInMessage(Message message, TelegramService telegramService) {
        this.message = message;
        this.telegramService = telegramService;
    }

  public void reply(SendMessage sendMessage) {
        sendMessage.setReplyToMessageId(message.getMessageId());
        sendMessage.setChatId(message.getChatId());
        telegramService.sendMessage(sendMessage);
  }

    public void reply(String messageText) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(messageText);
        reply(sendMessage);
    }
}
