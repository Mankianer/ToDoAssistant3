package de.mankianer.mankianerstelegramspringstarter;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Log4j2
@Component
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramBot extends TelegramLongPollingBot {

    private List<Consumer<Update>> updateHandlerFunctions = new ArrayList<>();

    private final TelegramProperties telegramProperties;

    public TelegramBot(TelegramProperties telegramProperties) {
        this.telegramProperties = telegramProperties;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if(!handleUserValidation(update)) return;
        log.info("Received update: {}", update);
        updateHandlerFunctions.forEach(f -> f.accept(update));
    }

    public void registerUpdateHandlerFunction(Consumer<Update> updateHandlerFunction) {
        updateHandlerFunctions.add(updateHandlerFunction);
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
        return (telegramProperties.getAllowedUsernames().size() == 0) || telegramProperties.getAllowedUsernames().contains(user.getUserName());
    }

    public void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Error sending message", e);
        }
    }

    @Override
    public String getBotUsername() {
        return telegramProperties.getUsername();
    }

    @Override
    public String getBotToken() {
        return telegramProperties.getToken();
    }
}
