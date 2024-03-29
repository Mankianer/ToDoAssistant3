package de.mankianer.todoassistant3.modules.trello;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.Card;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import de.mankianer.todoassistant3.Utils;
import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.todo.ToDo;
import de.mankianer.todoassistant3.core.models.todo.ToDoStatus;
import de.mankianer.todoassistant3.core.services.todo.ToDoAdapter;
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
public class TrelloService implements ToDoAdapter {

  private final Trello trelloApi;

  @Value("${trello.boardId}")
  private String boardId;

  private Board board;

  @Getter private TList planingList;
  private TList todoList;
  private TList runningList;
  private TList doneList;
  private ToDo2CardMapper toDo2CardMapper;

  public TrelloService(
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
    log.info("Trello board: {}", board.getName());
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

      log.info("Load Trello boardList - planingLists: {}", planingList.getName());
      log.info("Load Trello boardList - toDoList: {}", todoList.getName());
      log.info("Load Trello boardList - runningList: {}", runningList.getName());
      log.info("Load Trello boardList - doneList: {}", doneList.getName());
      return true;
    } else {
      log.error("Trello board has less than 4 lists");
      return false;
    }
  }

  public List<Card> getCards(TList list) {
    return trelloApi.getListCards(list.getId());
  }

  public List<Card> getPlaningCardsWithDueToday() {
    return trelloApi.getListCards(planingList.getId()).stream()
        .filter(card -> card.getDue() != null && Utils.isBevorOrToday(card.getDue()))
        .collect(Collectors.toList());
  }

  public String getPlaningCardsWithDueTodayAsMessageWithMarkdown() {
    List<Card> planingCardsWithDueToday = getPlaningCardsWithDueToday();
    if (planingCardsWithDueToday.size() == 0) {
      return "";
    }
    return "Folgende ToDos stehen in Planung, die heute eingeplant werden müssen:\n  Du kannst diese mit /plan erneut abfragen\n"
        + Utils.CardsToMarkdownMessage(planingCardsWithDueToday);
  }

  @Override
  public Optional<ToDo> loadToDo(String id) {
    Card card = trelloApi.getCard(id);
    return card != null ? Optional.of(toDo2CardMapper.mapCardToToDo(card)): Optional.empty();
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
