package de.mankianer.mankianerstelegramspringstarter.commands.models;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

public interface TelegramConversationInterface {

    public enum AbortReason {
        USER_ABORT,
        SYSTEM_ABORT,
        TIMEOUT,
        ERROR
    }

    SendMessage getMessage();
    void enterConversation();

    void onAnswer(String answer);
    void onAbort(AbortReason reason);

    Map<String, TelegramConversationInterface> getListenerMap();

    default boolean isEndOfConversation() {
        return getListenerMap().isEmpty();
    }

}
