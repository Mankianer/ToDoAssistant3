package de.mankianer.todoassistant3.modules.telegram.utils;

import org.telegram.telegrambots.meta.api.objects.Message;

public class TelegramUtils {
    public static de.mankianer.todoassistant3.core.models.communication.Message<Message> TELEGRAM_TO_MESSAGE(Message message) {
        if (message == null) return null;
        de.mankianer.todoassistant3.core.models.communication.Message<Message> previous = TELEGRAM_TO_MESSAGE(message.getReplyToMessage());

        return de.mankianer.todoassistant3.core.models.communication.Message.<Message>builder()
                .text(message.getText())
                .previous(previous)
                .build();
    }
}