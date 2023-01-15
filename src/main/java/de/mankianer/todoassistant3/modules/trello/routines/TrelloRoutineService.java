package de.mankianer.todoassistant3.modules.trello.routines;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import de.mankianer.todoassistant3.core.services.routines.RoutineAdapter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class TrelloRoutineService implements RoutineAdapter {

    private final Trello trelloApi;

    @Value("${trello.routines.boardId}")
    private String boardId;

    private Board board;

    private Reoutine2CardMapper routine2CardMapper;

    private TList inPlaningList;
    private TList scheduledList;
    private TList runningList;
    private TList disableList;

    public TrelloRoutineService(
            @Value("${trello.key}") String trelloKey,
            @Value("${trello.accessToken}") String trelloAccessToken) {
        trelloApi = new TrelloImpl(trelloKey, trelloAccessToken, new ApacheHttpClient());
        routine2CardMapper = new Reoutine2CardMapper();
    }

    /**
     * try to fetch Trello Data
     *
     * @return true if successful
     */
    public boolean loadTrelloData() {
        board = trelloApi.getBoard(boardId);
        log.info("Trello board: {}", board.getName());
        List<TList> boardLists = board.fetchLists();
        if (boardLists.size() >= 4) {
            inPlaningList = boardLists.get(0);
            scheduledList = boardLists.get(1);
            runningList = boardLists.get(2);
            disableList = boardLists.get(3);

            routine2CardMapper.addStatusMapping(inPlaningList.getId(), RoutineStatus.PLANNED);
            routine2CardMapper.addStatusMapping(scheduledList.getId(), RoutineStatus.SCHEDULED);
            routine2CardMapper.addStatusMapping(runningList.getId(), RoutineStatus.RUNNING);
            routine2CardMapper.addStatusMapping(disableList.getId(), RoutineStatus.DISABLED);

            log.info("Trello Routines lists:");
            log.info("  inPlaningList: {}", inPlaningList.getName());
            log.info("  scheduledList: {}", scheduledList.getName());
            log.info("  runningList: {}", runningList.getName());
            log.info("  disableList: {}", disableList.getName());
            return true;

        } else {
            log.error("Trello board has less than 4 lists");
            return false;
        }
    }

    @Override
    public Optional<Routine> load(String id) {
        Card card = trelloApi.getCard(id);
        return Optional.ofNullable(routine2CardMapper.mapCardToRoutine(card));
    }

    @Override
    public Optional<Routine> save(Routine routine) throws CouldNotCreateException {
        Card card = routine2CardMapper.mapRoutineToCard(routine, trelloApi);
        card = trelloApi.updateCard(card);
        return Optional.of(routine2CardMapper.mapCardToRoutine(card));
    }

    @Override
    public Optional<Routine> delete(String id) {
        Card card = trelloApi.getCard(id);
        card.delete();
        return Optional.of(routine2CardMapper.mapCardToRoutine(card));
    }

    @Override
    public List<Routine> loadAll() {
        return board.fetchCards().stream().map(routine2CardMapper::mapCardToRoutine).toList();
    }
}
