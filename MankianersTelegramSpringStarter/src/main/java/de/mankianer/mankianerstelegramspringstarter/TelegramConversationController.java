package de.mankianer.mankianerstelegramspringstarter;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversation;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversationInterface;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
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
//        if(conversation.isEndOfConversation()) {
//            //TODO: remove keyboard
//            ReplyKeyboardMarkup replyMarkup = new ReplyKeyboardMarkup();
//            KeyboardRow e1 = new KeyboardRow();
//            e1.add("N/A");
//            replyMarkup.setKeyboard(List.of(e1));
//            message.setReplyMarkup(replyMarkup);
//        } else {
//        }
        message.setReplyMarkup(getCustomKeyboard(conversation));
        if(conversation.isEndOfConversation()) {
            currentConversation = null;
            ReplyKeyboardRemove replyKeyboardRemove = ReplyKeyboardRemove.builder().removeKeyboard(true).build();
            message.setReplyMarkup(replyKeyboardRemove);
        }
       sendMessage(message);
    }

    public void abortCurrentConversation(TelegramConversationInterface.AbortReason reason) {
        if(currentConversation != null) {
            currentConversation.onAbort(reason);
            SendMessage abortMessage = getAbortMessage(reason);
            abortMessage.setChatId(currentConversation.getChatId());
            ReplyKeyboardRemove replyKeyboardRemove = ReplyKeyboardRemove.builder().removeKeyboard(true).build();
            abortMessage.setReplyMarkup(replyKeyboardRemove);
            sendMessage(abortMessage);
            currentConversation = null;
        }
    }

    private void sendMessage(SendMessage message) {
        if(currentConversation != null && currentConversation.getChatId() != null) {
            message.setChatId(currentConversation.getChatId());
            telegramBot.sendMessage(message);
        } else {
            telegramBot.broadcastMessage(message);
        }
    }

    public void onAnswer(String answer) {
        if(currentConversation != null) {
            if(answer.equals(DEFAULT_ABORT_OPTION)) {
                startConversation(getAbortConversation());
                return;
            }
            var nextConversation = currentConversation.onAnswer(answer);
            if(nextConversation != null) {
                startConversation(nextConversation);
            }else {
                currentConversation = null;
            }
        }
    }


    public boolean isConversationActive() {
        return currentConversation != null;
    }

    private TelegramConversation getAbortConversation() {
        return TelegramConversation.builder(ABORT_REQUEST_TEXT, currentConversation.getChatId())
                .on("Ja").onAnswer(() -> abortCurrentConversation(TelegramConversationInterface.AbortReason.USER_ABORT)).finish()
                .on("Nein").then(currentConversation).build();

    }

    private SendMessage getAbortMessage(TelegramConversationInterface.AbortReason reason) {
        SendMessage abortMessage = new SendMessage();
        abortMessage.setText("Konversation wurde abgebrochen, da " + reason.name() + "!");
        return abortMessage;
    }

    private ReplyKeyboardMarkup getCustomKeyboard(TelegramConversationInterface conversation) {
        KeyboardRow keyboardRow = new KeyboardRow();
        conversation.getOptions().forEach(keyboardRow::add);
        KeyboardRow defaultCustomKeyboardOptions = getDefaultCustomKeyboardOptions(!conversation.getMessage().getText().equals(ABORT_REQUEST_TEXT));
        List<KeyboardRow> keyboardRows = List.of(keyboardRow, defaultCustomKeyboardOptions);
        ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup.builder().keyboard(keyboardRows).oneTimeKeyboard(true).resizeKeyboard(true).selective(true).build();
        return keyboard;
    }

    private KeyboardRow getDefaultCustomKeyboardOptions(boolean withAbort) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add("/help");
        if(withAbort) {
            keyboardRow.add(DEFAULT_ABORT_OPTION);
        }
        return keyboardRow;
    }

}
