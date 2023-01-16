package de.mankianer.mankianerstelegramspringstarter.commands.models;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collection;

public interface TelegramConversationInterface {

    public enum AbortReason {
        USER_ABORT,
        SYSTEM_ABORT,
        TIMEOUT,
        ERROR
    }

    SendMessage getMessage();
    void enterConversation();

    TelegramConversationInterface onAnswer(String answer);
    void onAbort(AbortReason reason);

    Collection<String> getOptions();

    Long getChatId();

    default boolean isEndOfConversation() {
        return getOptions().isEmpty();
    }

}
