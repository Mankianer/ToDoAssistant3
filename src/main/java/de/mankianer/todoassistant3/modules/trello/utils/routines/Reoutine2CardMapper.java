package de.mankianer.todoassistant3.modules.trello.utils.routines;

import com.julienvey.trello.Trello;
import com.julienvey.trello.domain.Card;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import de.mankianer.todoassistant3.modules.trello.utils.TrelloUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
public class Reoutine2CardMapper {

    private Map<String, RoutineStatus> statusMap = new HashMap<>();
    private Map<RoutineStatus, String> listIdMap = new HashMap<>();

    public Routine mapCardToRoutine(@NonNull Card card) {
        Optional<CronExpression> cronExpressionOptional = getCronExpression(card);
        CronExpression cronExpression = cronExpressionOptional.orElse(CronExpression.parse("0 0 0 * * ?"));
        if(cronExpressionOptional.isEmpty()) {
            log.warn("Could not find cron expression in card: {}, use {} instead", card.getName(), cronExpression);
        }
        return Routine.builder()
                .id(card.getId())
                .name(card.getName())
                .description(card.getDesc())
                .status(statusMap.get(card.getIdList()))
                .cron(cronExpression)
                .url(card.getShortUrl())
                .nextExecution(getNextExecutionTime(card, cronExpression))
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
        card.setDue(TrelloUtils.convertToDateViaInstant(routine.getNextExecution()));
        return card;
    }

    private LocalDateTime getNextExecutionTime(Card card, CronExpression cron) {
        if(card.getDue() != null) {
            return card.getDue().toInstant().atZone(TimeZone.getDefault().toZoneId()).toLocalDateTime();
        }
        // get next execution time from cron expression
        return cron.next(LocalDateTime.now());
    }

    public Optional<CronExpression> getCronExpression(Card card) {
        Pattern pattern = Pattern.compile("cron:'(.*)'");
        Matcher m = pattern.matcher(card.getDesc());
        if(m.find()) {
            String cronExp = m.group(1);
            if(CronExpression.isValidExpression(cronExp)) {
                CronExpression.isValidExpression(cronExp);
                return Optional.of(CronExpression.parse(cronExp));
            }
        }
        log.warn("CronExpression konnte nicht in Card gefunden werden: " + card.getDesc());
        return Optional.empty();
    }

    public void addStatusMapping(String listId, RoutineStatus status){
        statusMap.put(listId, status);
        listIdMap.put(status, listId);
    }
}
