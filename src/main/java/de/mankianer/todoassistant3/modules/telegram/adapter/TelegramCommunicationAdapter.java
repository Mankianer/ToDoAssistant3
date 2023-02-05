package de.mankianer.todoassistant3.modules.telegram.adapter;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.todoassistant3.core.adapter.CommunicationAdapter;
import de.mankianer.todoassistant3.core.models.communication.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
public class TelegramCommunicationAdapter implements CommunicationAdapter {

    private TelegramService telegramService;

    public TelegramCommunicationAdapter(@Autowired TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    @Override
    public void sendMessage(Message message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message.getText());
        sendMessage.enableMarkdownV2(message.isMarkDown());

        if(message.getPrevious() != null) {
            if(message.getPrevious().getContext() instanceof org.telegram.telegrambots.meta.api.objects.Message) {
                org.telegram.telegrambots.meta.api.objects.Message telegramMessage = (org.telegram.telegrambots.meta.api.objects.Message) message.getPrevious().getContext();
                sendMessage.setReplyToMessageId(telegramMessage.getMessageId());
            }
        }

        telegramService.broadcastMessage(sendMessage);
    }
}
