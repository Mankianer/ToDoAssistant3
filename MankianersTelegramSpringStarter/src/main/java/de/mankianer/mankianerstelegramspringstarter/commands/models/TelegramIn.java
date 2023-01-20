package de.mankianer.mankianerstelegramspringstarter.commands.models;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;

public abstract class TelegramIn {
  protected TelegramService telegramService;

  public TelegramIn(TelegramService telegramService) {
    this.telegramService = telegramService;
  }

  public void reply(SendMessage sendMessage) {
    sendMessage.setReplyToMessageId(getMessage().getMessageId());
    sendMessage.setChatId(getMessage().getChatId());
    telegramService.sendMessage(sendMessage);
  }

  public void reply(String messageText) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.setText(messageText);
    reply(sendMessage);
  }

  public void replyAsMarkdown(String messageText) {
    this.replyAsMarkdown(messageText, null);
  }

  public void replyAsMarkdown(String messageText, ReplyKeyboardMarkup customKeyboard) {
    SendMessage sendMessage = new SendMessage();
    sendMessage.enableMarkdownV2(true);
    sendMessage.setText(messageText);

    if(customKeyboard != null) sendMessage.setReplyMarkup(customKeyboard);
    reply(sendMessage);
  }

  public abstract Message getMessage();
}
