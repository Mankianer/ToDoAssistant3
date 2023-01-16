package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversation;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversationInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.List;

public class TelegramConversationController {

    private static final String DEFAULT_ABORT_OPTION = "Abbrechen";
    private static final String ABORT_REQUEST_TEXT = "Willst du wirklich die Konversation abbrechen?";
    private TelegramConversationInterface currentConversation;
    private final TelegramBot telegramBot;

    public TelegramConversationController(TelegramBot telegramBot){
        this.telegramBot = telegramBot;
    }

    public void startConversation(TelegramConversationInterface conversation) {
        this.currentConversation = conversation;
        conversation.enterConversation();
        SendMessage message = conversation.getMessage();
        message.setReplyMarkup(getCustomKeyboard(conversation));
        telegramBot.sendMessage(message);
    }

    public void abortCurrentConversation(TelegramConversationInterface.AbortReason reason) {
        if(currentConversation != null) {
            currentConversation.onAbort(reason);
            currentConversation = null;
            telegramBot.sendMessage(getAbortMessage(reason));
        }
    }

    public void onAnswer(String answer) {
        if(currentConversation != null) {
            if(answer.equals(DEFAULT_ABORT_OPTION)) {
                startConversation(getAbortConversation());
                return;
            }
            currentConversation.onAnswer(answer);
            if(currentConversation.isEndOfConversation()) {
                currentConversation = null;
            } else {
                TelegramConversationInterface next = currentConversation.getListenerMap().get(answer);
                if(next != null) {
                    startConversation(next);
                } else {
                    // Error handling
                    SendMessage unknownAnswerMessage = new SendMessage();
                    unknownAnswerMessage.setText("Unbekannte Antwort: " + answer);
                    unknownAnswerMessage.setReplyMarkup(getCustomKeyboard(currentConversation));
                    telegramBot.sendMessage(unknownAnswerMessage);
                }
            }
        }
    }

    public boolean isConversationActive() {
        return currentConversation != null;
    }

    private TelegramConversation getAbortConversation() {
        return TelegramConversation.builder(ABORT_REQUEST_TEXT)
                .on("Ja").onAnswer(() -> abortCurrentConversation(TelegramConversationInterface.AbortReason.USER_ABORT)).finish()
                .on("Nein").onAnswer(() -> startConversation(currentConversation)).finish().build();

    }

    private SendMessage getAbortMessage(TelegramConversationInterface.AbortReason reason) {
        SendMessage abortMessage = new SendMessage();
        abortMessage.setText("Konversation wurde abgebrochen, da " + reason.name() + "!");
        return abortMessage;
    }

    private ReplyKeyboardMarkup getCustomKeyboard(TelegramConversationInterface conversation) {
        KeyboardRow keyboardRow = new KeyboardRow();
        conversation.getListenerMap().keySet().forEach(keyboardRow::add);
        KeyboardRow defaultCustomKeyboardOptions = getDefaultCustomKeyboardOptions(!conversation.getMessage().getText().equals(ABORT_REQUEST_TEXT));
        List<KeyboardRow> keyboardRows = List.of(keyboardRow, defaultCustomKeyboardOptions);
        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder().keyboard(keyboardRows).build();
        keyboard.setSelective(true);
        keyboard.setResizeKeyboard(true);
        keyboard.setOneTimeKeyboard(true);
        return keyboard;
    }

    private KeyboardRow getDefaultCustomKeyboardOptions(boolean withAbort) {
        KeyboardRow keyboardRow = new KeyboardRow();
        if(withAbort) {
            keyboardRow.add(DEFAULT_ABORT_OPTION);
        }
        return keyboardRow;
    }

}
