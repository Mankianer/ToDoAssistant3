package de.mankianer.todoassistant3.modules.telegram;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import de.mankianer.todoassistant3.controllers.ToDoController;
import de.mankianer.todoassistant3.models.message.Message;
import de.mankianer.todoassistant3.services.communication.CommunicationAdapter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Optional;

@Component
public class TelegramController implements CommunicationAdapter {

    private TelegramService telegramService;
    private ToDoController toDoController;

    public TelegramController(TelegramService telegramService, ToDoController toDoController) {
        this.telegramService = telegramService;
        this.toDoController = toDoController;
        telegramService.setMessageHandlerFunction(this::handleIncomingMessage);
    }

    public void handleIncomingMessage(TelegramInUpdate update) {
        String text = update.getUpdate().getMessage().getText();
        toDoController.createToDo(text, null, Optional.of(telegramToMessage(update.getMessage())));
    }

    private Message<org.telegram.telegrambots.meta.api.objects.Message> telegramToMessage(org.telegram.telegrambots.meta.api.objects.Message message) {
        if(message == null) return null;
        Message<org.telegram.telegrambots.meta.api.objects.Message> previous = telegramToMessage(message.getReplyToMessage());

        return Message.<org.telegram.telegrambots.meta.api.objects.Message>builder()
                .text(message.getText())
                .previous(previous)
                .build();
    }

    @Override
    public void sendMessage(Message message){
        String msgMarker = "#" + message.getId() + "\n";
        String msg = msgMarker + message.getText();

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(msg);
        sendMessage.enableMarkdownV2(message.isMarkDown());

        if(message.getPrevious() != null) {
            if(message.getPrevious().getContext().getContext() instanceof org.telegram.telegrambots.meta.api.objects.Message) {
                org.telegram.telegrambots.meta.api.objects.Message telegramMessage = (org.telegram.telegrambots.meta.api.objects.Message) message.getPrevious().getContext().getContext();
                sendMessage.setReplyToMessageId(telegramMessage.getMessageId());
            }
        }

        telegramService.broadcastMessage(sendMessage);
    }

}
