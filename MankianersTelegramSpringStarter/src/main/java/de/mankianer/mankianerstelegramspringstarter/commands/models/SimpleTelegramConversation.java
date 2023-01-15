package de.mankianer.mankianerstelegramspringstarter.commands.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class SimpleTelegramConversation implements TelegramConversationInterface {

    @NonNull
    @Getter
    @Setter
    private String messageText;

    @Setter
    private Map<String,TelegramConversationInterface> listenerMap = new HashMap<>();

    @Setter
    private Consumer<AbortReason> onAbort;
    @Setter
    private Runnable onEnter;

    @Setter
    private Consumer<String> onAnswer;

    public void addConversation(String key, TelegramConversationInterface conversation) {
        listenerMap.put(key, conversation);
    }

    @Override
    public SendMessage getMessage() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(messageText);
        return sendMessage;
    }

    @Override
    public void enterConversation() {
        if(onEnter != null) {
            onEnter.run();
        }
    }

    @Override
    public void onAnswer(String answer) {
        if(onAnswer != null) {
            onAnswer.accept(answer);
        }
    }

    @Override
    public void onAbort(AbortReason reason) {
        if(onAbort != null) {
            onAbort.accept(reason);
        }
    }

    @Override
    public Map<String, TelegramConversationInterface> getListenerMap() {
        return listenerMap;
    }

    public static SimpleTelegramBuilder builder(String messageText) {
        return new SimpleTelegramBuilder(messageText);
    }

    public static class SimpleTelegramBuilder {
        private String messageText;
        private final Map<String, Map.Entry<TelegramConversationInterface, Runnable>> listenerMap = new HashMap<>();
        private Consumer<AbortReason> onAbort;
        private Runnable onEnter;

        private SimpleTelegramBuilder(String messageText) {
            this.messageText = messageText;
        }

        public SimpleTelegramBuilder messageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public SimpleTelegramBuilder addConversation(String answer, TelegramConversationInterface conversation,@NonNull Runnable onAnswer) {
            this.listenerMap.put(answer, Map.entry(conversation, onAnswer));
            return this;
        }

        public SimpleTelegramBuilder addConversation(String answer, TelegramConversationInterface conversation) {
            return this.addConversation(answer, conversation, () -> {});
        }

        public SimpleTelegramBuilder onAbort(Consumer<AbortReason> onAbort) {
            this.onAbort = onAbort;
            return this;
        }

        public SimpleTelegramBuilder onEnter(Runnable onEnter) {
            this.onEnter = onEnter;
            return this;
        }

        public SimpleTelegramConversation build() {
            SimpleTelegramConversation simpleTelegramConversation = new SimpleTelegramConversation(messageText);
            this.listenerMap.forEach((key, value) -> simpleTelegramConversation.addConversation(key, value.getKey()));
            simpleTelegramConversation.setOnAnswer(s -> {
                var entry = listenerMap.get(s);
                if(entry != null) {
                    entry.getValue().run();
                }
            });
            simpleTelegramConversation.setOnAbort(onAbort);
            simpleTelegramConversation.setOnEnter(onEnter);
            return simpleTelegramConversation;
        }
    }
}
