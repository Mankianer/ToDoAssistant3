package de.mankianer.todoassistant3.modules.trello.adapter;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import de.mankianer.todoassistant3.core.adapter.ToDoAdapter;
import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.todos.ToDo;
import de.mankianer.todoassistant3.core.models.todos.ToDoStatus;
import de.mankianer.todoassistant3.modules.trello.utils.todos.ToDo2CardMapper;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
public class TrelloToDoAdapter implements ToDoAdapter {

  private final Trello trelloApi;

  @Value("${trello.todo.boardId}")
  private String boardId;

  private Board board;

  @Getter private TList planingList;
  private TList todoList;
  private TList runningList;
  private TList doneList;
  private ToDo2CardMapper toDo2CardMapper;

  public TrelloToDoAdapter(
      @Value("${trello.key}") String trelloKey,
      @Value("${trello.accessToken}") String trelloAccessToken) {
    trelloApi = new TrelloImpl(trelloKey, trelloAccessToken, new ApacheHttpClient());
    toDo2CardMapper = new ToDo2CardMapper();
  }

  @PostConstruct
  public void init() {}

  /**
   * try to fetch Trello Data
   *
   * @return true if successful
   */
  public boolean loadTrelloData() {
    board = trelloApi.getBoard(boardId);
    log.info("Trello ToDo board: {}", board.getName());
    List<TList> boardLists = board.fetchLists();
    if (boardLists.size() >= 4) {
      planingList = boardLists.get(0);
      todoList = boardLists.get(1);
      runningList = boardLists.get(2);
      doneList = boardLists.get(3);
      toDo2CardMapper.addStatusMapping(planingList.getId(), ToDoStatus.IN_PLANING);
      toDo2CardMapper.addStatusMapping(todoList.getId(), ToDoStatus.TODO);
      toDo2CardMapper.addStatusMapping(runningList.getId(), ToDoStatus.IN_PROGRESS);
      toDo2CardMapper.addStatusMapping(doneList.getId(), ToDoStatus.DONE);

      log.info("Load Trello Todo boardList - planingLists: {}", planingList.getName());
      log.info("Load Trello Todo boardList - toDoList: {}", todoList.getName());
      log.info("Load Trello Todo boardList - runningList: {}", runningList.getName());
      log.info("Load Trello Todo boardList - doneList: {}", doneList.getName());
      return true;
    } else {
      log.error("Trello board has less than 4 lists");
      return false;
    }
  }

  public List<Card> getCards(TList list) {
    return trelloApi.getListCards(list.getId());
  }

  @Override
  public Optional<ToDo> loadToDo(String id) {
    Card card = trelloApi.getCard(id);
    return Optional.ofNullable(toDo2CardMapper.mapCardToToDo(card));
  }

  @Override
  public ToDo saveToDo(ToDo toDo) throws CouldNotCreateException {
    Card card = toDo2CardMapper.mapToDoToCard(toDo, trelloApi);
    card = trelloApi.updateCard(card);
    return Optional.of(toDo2CardMapper.mapCardToToDo(card)).get();
  }

  @Override
  public void deleteToDo(String id) {
    trelloApi.deleteCard(id);
  }

  @Override
  public List<ToDo> loadAllToDos() {
    return board.fetchCards().stream().map(toDo2CardMapper::mapCardToToDo).collect(Collectors.toList());
  }
}
