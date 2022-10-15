package de.mankianer.todoassistant3.modules.telegram;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import de.mankianer.todoassistant3.controllers.ToDoController;
import org.springframework.stereotype.Service;

@Service
public class TelegramController {

    private ToDoController toDoController;

    public TelegramController(TelegramService telegramService, ToDoController toDoController) {
        this.toDoController = toDoController;
        telegramService.setMessageHandlerFunction(this::handleIncomingMessage);
    }

    public void handleIncomingMessage(TelegramInUpdate update) {
        String text = update.getUpdate().getMessage().getText();
        toDoController.createToDo(text, null);
        update.reply("Karte wurde erstellt: (URL) " + text);
    }
}
