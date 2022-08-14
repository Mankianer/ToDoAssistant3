package de.mankianer.todoassistant3.services;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Board;
import com.julienvey.trello.domain.TList;
import com.julienvey.trello.impl.TrelloImpl;
import com.julienvey.trello.impl.http.ApacheHttpClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;

@Log4j2
@Service
public class TrelloService {

  private final Trello trelloApi;

  @Value("${trello.boardId}")
  private String boardId;

  private Board board;

  private TList planingList;
  private TList todoList;
  private TList runningList;
  private TList doneList;

  public TrelloService(
      @Value("${trello.key}") String trelloKey,
      @Value("${trello.accessToken}") String trelloAccessToken) {
    trelloApi = new TrelloImpl(trelloKey, trelloAccessToken, new ApacheHttpClient());
  }

  @PostConstruct
  public void init() {
  }

  /**
   * try to fetch Trello Data
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
}
