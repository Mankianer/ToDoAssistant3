package de.mankianer.todoassistant3.telegram;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Log4j2
@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    @Getter
    private String botToken;

    @Value("${telegram.bot.username}")
    @Getter
    private String botUsername;

    @Value("${telegram.bot.allowedUsernames:}")
    @Getter
    private List<String> allowedUsernames;

    @Override
    public void onUpdateReceived(Update update) {
        if(!handleUserValidation(update)) return;
        log.info("Received update: {}", update);
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(update.getMessage().getChatId());
        sendMessage.setText("Hallo!");
        sendMessage(sendMessage);
    }

    /**
     * Sends a message to the user. If the user is not allowed to send messages.
     * @param update
     * @return true if the user is allowed
     */
    private boolean handleUserValidation(Update update) {
        if(!isUserAllowed(update.getMessage().getFrom())) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Ich soll nicht mit Fremden reden! \nUnd dich kenn ich nicht!");
            sendMessage(sendMessage);
            return false;
        }
        return true;
    }

    /**
     * checks if user is allowed to send messages
     * @param user
     * @return true if the user is allowed
     */
    public boolean isUserAllowed(User user) {
        return (allowedUsernames.size() == 0) || allowedUsernames.contains(user.getUserName());
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }
}
