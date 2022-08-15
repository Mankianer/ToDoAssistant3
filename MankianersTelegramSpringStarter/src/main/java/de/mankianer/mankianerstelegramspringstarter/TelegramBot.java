package de.mankianer.mankianerstelegramspringstarter;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
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
public class TelegramBot extends TelegramLongPollingCommandBot {

    private List<Consumer<Update>> updateHandlerFunctions = new ArrayList<>();

    private final TelegramProperties telegramProperties;

    private final UserHandler fileUserHandler;

    public TelegramBot(TelegramProperties telegramProperties, FileUserHandler fileUserHandler) {
        this.telegramProperties = telegramProperties;
        this.fileUserHandler = fileUserHandler;
    }

    public void registerUpdateHandlerFunction(Consumer<Update> updateHandlerFunction) {
        updateHandlerFunctions.add(updateHandlerFunction);
    }

    /**
     * Sends a message to the user if Update was handled.
     * @param message
     * @return true if Update is not handled, false if Update is handled.
     */
    public boolean handleUserValidation(Message message) {
        User user = message.getFrom();
        if(!isUserAllowed(user)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Ich soll nicht mit Fremden reden! \nUnd dich kenn ich nicht!");
            sendMessage(sendMessage);
            return false;
        }
        if (handleIfIsRegisterMessage(message)) return false;
        if(!fileUserHandler.isUserRegistered(user)) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Hallo " + user.getFirstName() + "! \nIch bin Mankianers ToDoAssistetBot, bitte registriere dich mit /start");
            sendMessage(sendMessage);
            return false;
        }
        return true;
    }

    private boolean handleIfIsRegisterMessage(Message message) {
        if(isRegisterMessage(message) && !fileUserHandler.isUserRegistered(message.getFrom())) {
            fileUserHandler.registerUser(message);
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Hallo " + message.getFrom().getFirstName() + "! \nDu bist nun Registriert! \nUm dich wieder abzumelden gebe /stop ein.");
            sendMessage(sendMessage);
            return true;
        }
        if(isUnregisterMessage(message) && fileUserHandler.isUserRegistered(message.getFrom())) {
            fileUserHandler.unregisterUser(message.getFrom());
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(message.getChatId());
            sendMessage.setText("Du hast dich erfolgreich abgemeldet!");
            sendMessage(sendMessage);
            return true;
        }
        return false;
    }

    private boolean isRegisterMessage(Message message) {
        return message.getText().equals("/start");
    }

    private boolean isUnregisterMessage(Message message) {
        return message.getText().equals("/stop");
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
            log.error("Error while sending message", e);
        }
    }


    public void broadcastMessage(SendMessage message) {
        fileUserHandler.forEach((username, chatId) -> {
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

    @Override
    public void processNonCommandUpdate(Update update) {
        if(!handleUserValidation(update.getMessage())) return;
        log.info("Command not found: {}", update);
        updateHandlerFunctions.forEach(f -> f.accept(update));
    }
}
