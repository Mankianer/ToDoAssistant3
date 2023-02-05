package de.mankianer.todoassistant3.modules.telegram;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import de.mankianer.todoassistant3.core.adapter.CommunicationAdapter;
import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.communication.Message;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.todos.ToDo;
import de.mankianer.todoassistant3.core.services.RoutineService;
import de.mankianer.todoassistant3.core.services.ToDoService;
import de.mankianer.todoassistant3.modules.telegram.commands.routines.GetPlanedRoutines;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import javax.annotation.PostConstruct;
import java.util.List;

@Component
public class TelegramController implements CommunicationAdapter {

    private TelegramService telegramService;
    private ToDoService toDoService;
    private RoutineService routineService;

    public TelegramController(TelegramService telegramService, ToDoService toDoService, RoutineService routineService) {
        this.telegramService = telegramService;
        this.toDoService = toDoService;
        this.routineService = routineService;
    }

    @PostConstruct
    public void init() {
        telegramService.setMessageHandlerFunction(this::handleIncomingMessage);
        routineService.setRoutineUpdateToScheduleListener(this::handleRoutinesUpdateToScheduleEvent);
    }

    public void handleIncomingMessage(TelegramInUpdate update) {
        String text = update.getUpdate().getMessage().getText();
        try {
            ToDo toDo = toDoService.createToDo(text, null);
            update.reply("ToDo wurde erstellt: " + toDo.getUrl());
        } catch (CouldNotCreateException e) {
            update.reply("ToDo konnte nicht erstellt werden: \n" + e.getMessage());
        }
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

        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(message.getText());
        sendMessage.enableMarkdownV2(message.isMarkDown());

        if(message.getPrevious() != null) {
            if(message.getPrevious().getContext() instanceof org.telegram.telegrambots.meta.api.objects.Message) {
                org.telegram.telegrambots.meta.api.objects.Message telegramMessage = (org.telegram.telegrambots.meta.api.objects.Message) message.getPrevious().getContext();
                sendMessage.setReplyToMessageId(telegramMessage.getMessageId());
            }
        }

        telegramService.broadcastMessage(sendMessage);
    }

    private void handleRoutinesUpdateToScheduleEvent(List<Routine> routines) {
        telegramService.broadcastMessageAsMarkdown(GetPlanedRoutines.getMessageText(routines, routineService.getUrlToData()));
    }

}
