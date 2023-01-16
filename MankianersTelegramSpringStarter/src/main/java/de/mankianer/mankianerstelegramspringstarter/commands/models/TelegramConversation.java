package de.mankianer.mankianerstelegramspringstarter.commands.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Collection;
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

    @Getter
    @Setter
    @NonNull
    private Long chatId;

    public void addConversation(String key, TelegramConversationInterface conversation) {
        listenerMap.put(key, conversation);
    }

    @Override
    public SendMessage getMessage() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(messageText);
        sendMessage.setChatId(chatId);
        return sendMessage;
    }

    @Override
    public void enterConversation() {
        if(onEnter != null) {
            onEnter.run();
        }
    }

    @Override
    public TelegramConversationInterface onAnswer(String answer) {
        if(onAnswer != null && listenerMap.containsKey(answer)) {
            onAnswer.accept(answer);
            return listenerMap.get(answer);
        }
        return unknownAnswerConversation(answer, this);
    }

    @Override
    public void onAbort(AbortReason reason) {
        if(onAbort != null) {
            onAbort.accept(reason);
        }
    }

    @Override
    public Collection<String> getOptions() {
        return listenerMap.keySet();
    }

    public static TelegramConversationBuilder<?> builder(String messageText, Long chatId) {
        return new TelegramConversationBuilder<>(messageText, chatId);
    }

    public static TelegramConversation unknownAnswerConversation(String user_answer, TelegramConversationInterface continueConversation) {
        return builder("Ich konnte '" + user_answer + "' nicht zu ordnen!", continueConversation.getChatId()).on("Wiederholen").then(continueConversation).build();
    }

    public static class TelegramConversationBuilder<T extends TelegramConversationBuilder<?>> {
        protected String messageText;
        protected final Map<String, Map.Entry<TelegramConversationInterface, Runnable>> listenerMap = new HashMap<>();
        protected Consumer<AbortReason> onAbort;
        protected Runnable onEnter;

        protected Long chatId;

        private TelegramConversationBuilder(String messageText, Long chatId) {
            this.messageText = messageText;
            this.chatId = chatId;
        }

        public ConversationAnswerBuilder<T> on(String answer) {
            return new ConversationAnswerBuilder<>(this, answer);
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
            TelegramConversation telegramConversation = new TelegramConversation(messageText, chatId);
            this.listenerMap.forEach((key, value) -> {
                telegramConversation.addConversation(key, value.getKey());
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

    public static class ConversationAnswerBuilder<P extends TelegramConversationBuilder<?>> {
        private final TelegramConversationBuilder<P> telegramConversationBuilder;
        private final String answer;
        private Runnable onAnswer = () -> {};

        public ConversationAnswerBuilder(TelegramConversationBuilder<P> telegramConversationBuilder, String answer) {
            this.telegramConversationBuilder = telegramConversationBuilder;
            this.answer = answer;
        }

        public ConversationAnswerBuilder<P> onAnswer(Runnable onAnswer) {
            this.onAnswer = onAnswer;
            return this;
        }

        public SubTelegramConversationBuilder<?, P> then(String messageText) {
            return new SubTelegramConversationBuilder<>(telegramConversationBuilder, messageText, this);
        }

        public TelegramConversationBuilder<P> then(TelegramConversationInterface conversation) {
            telegramConversationBuilder.addConversation(answer, conversation, onAnswer);
            return telegramConversationBuilder;
        }

        public TelegramConversationBuilder<P> finish() {
            telegramConversationBuilder.addConversation(answer, null, onAnswer);
            return telegramConversationBuilder;
        }

    }


    public static class SubTelegramConversationBuilder<T extends SubTelegramConversationBuilder<?, ?>, P extends TelegramConversationBuilder<?>> extends TelegramConversationBuilder<T> {
        private final TelegramConversationBuilder<P> telegramConversationBuilder;
        private final ConversationAnswerBuilder<P> answerBuilder;

        public SubTelegramConversationBuilder(TelegramConversationBuilder<P> telegramConversationBuilder,
                                              String messageText, ConversationAnswerBuilder<P> answerBuilder) {
            super(messageText, telegramConversationBuilder.chatId);
            this.telegramConversationBuilder = telegramConversationBuilder;
            this.answerBuilder = answerBuilder;
        }

        public TelegramConversationBuilder<P> finish() {
            this.telegramConversationBuilder.addConversation(answerBuilder.answer, super.build(), answerBuilder.onAnswer);
            return telegramConversationBuilder;
        }

        public TelegramConversationBuilder<?> finishToMain() {
            var builder = this.telegramConversationBuilder;
            while (builder instanceof SubTelegramConversationBuilder) {
                builder = ((SubTelegramConversationBuilder<T, P>) builder).finish();
            }
            return builder;
        }

        @Override
        public TelegramConversation build() {
            return finish().build();
        }

    }
}
