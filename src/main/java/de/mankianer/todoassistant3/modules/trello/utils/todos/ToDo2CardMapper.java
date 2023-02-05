package de.mankianer.todoassistant3.modules.trello.utils.todos;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import de.mankianer.todoassistant3.core.models.todos.ToDo;
import de.mankianer.todoassistant3.core.models.todos.ToDoStatus;
import de.mankianer.todoassistant3.modules.trello.utils.TrelloUtils;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ToDo2CardMapper {

    private Map<String, ToDoStatus> statusMap = new HashMap<>();
    private Map<ToDoStatus, String> listIdMap = new HashMap<>();

    public ToDo mapCardToToDo(@NonNull Card card) {
        ToDo.ToDoBuilder builder = ToDo.builder();
        if(card.getDue() != null) builder.dueDate(TrelloUtils.convertToLocalDateTimeViaInstant(card.getDue()));
        return builder
                .id(card.getId())
                .name(card.getName())
                .description(card.getDesc())
                .status(statusMap.get(card.getIdList()))
                .url(card.getShortUrl())
                .build();

    }

    public Card mapToDoToCard(@NonNull ToDo toDo, Trello trello) {
        Card card;
        if(toDo.getId() == null) {
            card = new Card();
            card = trello.createCard(listIdMap.get(toDo.getStatus()), card);
        } else {
            card = trello.getCard(toDo.getId());
        }
        card.setName(toDo.getName());
        card.setDesc(toDo.getDescription());
        card.setIdList(listIdMap.get(toDo.getStatus()));
        if(toDo.getDueDate() != null) card.setDue(TrelloUtils.convertToDateViaInstant(toDo.getDueDate()));
        return card;
    }

    public void addStatusMapping(String listId, ToDoStatus status){
        statusMap.put(listId, status);
        listIdMap.put(status, listId);
    }
}
