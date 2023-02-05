package de.mankianer.todoassistant3.core.utils;

import de.mankianer.todoassistant3.core.models.todos.ToDo;

import java.util.List;

public class ToDoUtils {
    public static String TodosToMarkdownMessage(List<ToDo> todos) {
        String ret = "";
        for (ToDo todo : todos) {
            ret += String.format("\n  \\*__%s__: *%s* ", todo.getId(), todo.getName());
        }
        return ret;
    }

    public static String getPlaningToDosWithDueTodayAsMessageWithMarkdown(List<ToDo> toDos) {
        if (toDos.size() == 0) {
            return "";
        }
        return "Folgende ToDos stehen in Planung, die heute eingeplant werden müssen:\n  Du kannst diese mit /plan erneut abfragen\n"
                + TodosToMarkdownMessage(toDos);
    }
}