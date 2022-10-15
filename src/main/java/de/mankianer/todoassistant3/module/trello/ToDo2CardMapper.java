package de.mankianer.todoassistant3.module.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import de.mankianer.todoassistant3.Utils;
import de.mankianer.todoassistant3.model.todo.ToDo;
import de.mankianer.todoassistant3.model.todo.ToDoStatus;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ToDo2CardMapper {

    private Map<String, ToDoStatus> statusMap = new HashMap<>();
    private Map<ToDoStatus, String> listIdMap = new HashMap<>();

    public ToDo mapCardToToDo(@NonNull Card card) {
        return ToDo.builder()
                .id(card.getId())
                .name(card.getName())
                .description(card.getDesc())
                .status(statusMap.get(card.getIdList()))
                .dueDate(Utils.convertToLocalDateTimeViaInstant(card.getDue()))
                .build();
    }

    public Card mapToDoToCard(@NonNull ToDo toDo, Trello trello) {
        Card card = trello.getCard(toDo.getId());
        card.setName(toDo.getName());
        card.setDesc(toDo.getDescription());
        card.setIdList(listIdMap.get(toDo.getStatus()));
        card.setDue(Utils.convertToDateViaInstant(toDo.getDueDate()));
        return card;
    }

    public void addStatusMapping(String listId, ToDoStatus status){
        statusMap.put(listId, status);
        listIdMap.put(status, listId);
    }
}
