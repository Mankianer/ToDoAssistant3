package de.mankianer.todoassistant3.core.services.routines;

import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

@Log4j2
@Service
public class RoutineService {

    private final RoutineAdapter routineAdapter;

    public RoutineService(@Autowired RoutineAdapter routineAdapter) {
        this.routineAdapter = routineAdapter;
    }

    @Value("${routine.updateSchedule.cron.expression}")
    private String updateScheduledRoutinesCronExpressionStr;
    private CronExpression updateScheduledRoutinesCronExpression;

    @Setter
    private Consumer<List<Routine>> routineUpdateListener = (routines -> log.debug("No routine update listener set"));


    @PostConstruct
    public void init() {
        updateScheduledRoutinesCronExpression = CronExpression.parse(updateScheduledRoutinesCronExpressionStr);
    }

    public Routine createToDo(String name, String description) throws CouldNotCreateException {
        Routine routine = Routine.builder()
                .name(name)
                .description(description)
                .nextExecution(LocalDateTime.now())
                .status(RoutineStatus.PLANNED)
                .build();
        return this.routineAdapter.save(routine).orElse(null);
    }


    private LocalDateTime getNextUpdateSheduled() {
        return updateScheduledRoutinesCronExpression.next(LocalDateTime.now());
    }


    public Stream<Routine> getAllRoutinesByStatus(RoutineStatus status) {
        return getAllRoutines().filter(routine -> status.equals(routine.getStatus()));
    }

    public Stream<Routine> getAllRoutines() {
        return this.routineAdapter.loadAll().stream();
    }

    @Scheduled(cron = "${routine.updateSchedule.cron.expression}")
    public void updateRoutinesToSchedule() {
        List<Routine> routines = getAllRoutinesByStatus(RoutineStatus.PLANNED).filter(routine -> routine.getNextExecution().isBefore(getNextUpdateSheduled())).toList();
        routines.forEach(routine -> {
            routine.setStatus(RoutineStatus.SCHEDULED);
            try {
                routineAdapter.save(routine);
            } catch (CouldNotCreateException e) {
                log.error("Could not update routine", e);
            }
        });
        routineUpdateListener.accept(routines);
    }
}
