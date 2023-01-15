package de.mankianer.todoassistant3.modules.trello.routines;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;

public class Reoutine2CardMapper {

    private Map<String, RoutineStatus> statusMap = new HashMap<>();
    private Map<RoutineStatus, String> listIdMap = new HashMap<>();

    public Routine mapCardToRoutine(@NonNull Card card) {
        return Routine.builder()
                .id(card.getId())
                .name(card.getName())
                .description(card.getDesc())
                .status(RoutineStatus.valueOf(card.getIdList()))
                .url(card.getShortUrl())
                .build();
    }

    public Card mapRoutineToCard(@NonNull Routine routine, Trello trello) {
        Card card;
        if(routine.getId() == null) {
            card = new Card();
            card = trello.createCard(listIdMap.get(routine.getStatus()), card);
        } else {
            card = trello.getCard(routine.getId());
        }
        card.setName(routine.getName());
        card.setDesc(routine.getDescription());
        card.setIdList(listIdMap.get(routine.getStatus()));
        return card;
    }

    public void addStatusMapping(String listId, RoutineStatus status){
        statusMap.put(listId, status);
        listIdMap.put(status, listId);
    }
}
