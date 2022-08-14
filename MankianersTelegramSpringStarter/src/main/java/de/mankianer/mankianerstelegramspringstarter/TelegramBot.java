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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Log4j2
@Component
@EnableConfigurationProperties(TelegramProperties.class)
public class TelegramBot extends TelegramLongPollingBot {

    private Map<String, String> registerUserChatIdMap = new HashMap<>();

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
     * Sends a message to the user if Update was handled.
     * @param update
     * @return true if Update is not handled, false if Update is handled.
     */
    private boolean handleUserValidation(Update update) {
        User user = update.getMessage().getFrom();
        if(!isUserAllowed(user)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Ich soll nicht mit Fremden reden! \nUnd dich kenn ich nicht!");
            sendMessage(sendMessage);
            return false;
        }
        if (handleIfIsRegisterMessage(update)) return false;
        if(!isUserRegistered(user)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Hallo " + user.getFirstName() + "! \nIch bin Mankianers ToDoAssistetBot, bitte registriere dich mit /start");
            sendMessage(sendMessage);
            return false;
        }
        return true;
    }

    /**
     *
     * @param update
     * @return
     */
    private boolean handleIfIsRegisterMessage(Update update) {
        if(isRegisterMessage(update) && !isUserRegistered(update.getMessage().getFrom())) {
            registerUser(update);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Hallo " + update.getMessage().getFrom().getFirstName() + "! \nDu bist nun Registriert! \nUm dich wieder abzumelden gebe /stop ein.");
            sendMessage(sendMessage);
            return true;
        }
        if(isUnregisterMessage(update) && isUserRegistered(update.getMessage().getFrom())) {
            unregisterUser(update.getMessage().getFrom());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(update.getMessage().getChatId());
            sendMessage.setText("Du hast dich erfolgreich abgemeldet!");
            sendMessage(sendMessage);
            return true;
        }
        return false;
    }

    private void unregisterUser(User user) {
        registerUserChatIdMap.remove(user.getUserName());
    }

    private void registerUser(Update update) {
        registerUserChatIdMap.put(update.getMessage().getFrom().getUserName(), update.getMessage().getChatId().toString());
    }

    private boolean isRegisterMessage(Update update) {
        return update.getMessage().getText().equals("/start");
    }

    private boolean isUnregisterMessage(Update update) {
        return update.getMessage().getText().equals("/stop");
    }

    public boolean isUserRegistered(User user) {
        return registerUserChatIdMap.containsKey(user.getUserName());
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


    public void broadcastMessage(SendMessage message) {
        registerUserChatIdMap.forEach((username, chatId) -> {
            message.setChatId(chatId);
            sendMessage(message);
        });
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
