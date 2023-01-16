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
public class TelegramConversation implements TelegramConversationInterface {

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

    public static TelegramConversationBuilder<?> builder(String messageText) {
        return new TelegramConversationBuilder<>(messageText);
    }

    public static class TelegramConversationBuilder<T extends TelegramConversationBuilder<?>> {
        protected String messageText;
        protected final Map<String, Map.Entry<TelegramConversationInterface, Runnable>> listenerMap = new HashMap<>();
        protected Consumer<AbortReason> onAbort;
        protected Runnable onEnter;

        private TelegramConversationBuilder(String messageText) {
            this.messageText = messageText;
        }

        public TelegramConversationBuilder<T> messageText(String messageText) {
            this.messageText = messageText;
            return this;
        }

        public ConversationAnswerBuilder on(String answer) {
            return new ConversationAnswerBuilder(this, answer);
        }

        public TelegramConversationBuilder<T> addConversation(String answer, TelegramConversationInterface conversation, Runnable onAnswer) {
            listenerMap.put(answer, new HashMap.SimpleEntry<>(conversation, onAnswer));
            return this;
        }

        public TelegramConversationBuilder<T> addConversation(String answer, TelegramConversationInterface conversation) {
            return this.addConversation(answer, conversation, () -> {});
        }

        public TelegramConversationBuilder<T> onAbort(Consumer<AbortReason> onAbort) {
            this.onAbort = onAbort;
            return this;
        }

        public TelegramConversationBuilder<T> onEnter(Runnable onEnter) {
            this.onEnter = onEnter;
            return this;
        }

        public TelegramConversation build() {
            TelegramConversation telegramConversation = new TelegramConversation(messageText);
            this.listenerMap.forEach((key, value) -> {
                if(value.getKey() != null){
                    telegramConversation.addConversation(key, value.getKey());
                }
            });
            telegramConversation.setOnAnswer(s -> {
                var entry = listenerMap.get(s);
                if(entry != null) {
                    entry.getValue().run();
                }
            });
            telegramConversation.setOnAbort(onAbort);
            telegramConversation.setOnEnter(onEnter);
            return telegramConversation;
        }
    }

    public static class ConversationAnswerBuilder {
        private final TelegramConversationBuilder<?> telegramConversationBuilder;
        private final String answer;
        private Runnable onAnswer = () -> {};

        public ConversationAnswerBuilder(TelegramConversationBuilder<?> telegramConversationBuilder, String answer) {
            this.telegramConversationBuilder = telegramConversationBuilder;
            this.answer = answer;
        }

        public ConversationAnswerBuilder onAnswer(Runnable onAnswer) {
            this.onAnswer = onAnswer;
            return this;
        }

        public SubTelegramConversationBuilder<?> then(String messageText) {
            return new SubTelegramConversationBuilder<>(telegramConversationBuilder, messageText, this);
        }

        public TelegramConversationBuilder<?> finish() {
            telegramConversationBuilder.addConversation(answer, null, onAnswer);
            return telegramConversationBuilder;
        }

    }


    public static class SubTelegramConversationBuilder<T extends SubTelegramConversationBuilder<?>> extends TelegramConversationBuilder<T> {
        private final TelegramConversationBuilder<?> telegramConversationBuilder;
        private final ConversationAnswerBuilder answerBuilder;

        public SubTelegramConversationBuilder(TelegramConversationBuilder<?> telegramConversationBuilder,
                                              String messageText, ConversationAnswerBuilder answerBuilder) {
            super(messageText);
            this.telegramConversationBuilder = telegramConversationBuilder;
            this.answerBuilder = answerBuilder;
        }

        public TelegramConversationBuilder<?> finish() {
            this.telegramConversationBuilder.addConversation(answerBuilder.answer, super.build(), answerBuilder.onAnswer);
            return telegramConversationBuilder;
        }

        @Override
        public TelegramConversation build() {
            return finish().build();
        }

    }
}
