package de.mankianer.todoassistant3.modules.telegram.handler;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramInUpdate;
import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.todos.ToDo;
import de.mankianer.todoassistant3.core.services.ToDoService;

import java.util.function.Consumer;

public class TelegramEventHandler {

    public static Consumer<TelegramInUpdate> handleIncomingMessage(ToDoService toDoService) {
        return update -> {
            String text = update.getUpdate().getMessage().getText();
            try {
                ToDo toDo = toDoService.createToDo(text, null);
                update.reply("ToDo wurde erstellt: " + toDo.getUrl());
            } catch (CouldNotCreateException e) {
                update.reply("ToDo konnte nicht erstellt werden: \n" + e.getMessage());
            }
        };
    }
}
