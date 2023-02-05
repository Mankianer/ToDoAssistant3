package de.mankianer.todoassistant3.core.services.routines;

import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @Setter
    private Consumer<List<Routine>> routineUpdateToScheduleListener = (routines -> log.debug("No routine update listener set"));

    public Routine createToDo(String name, String description) throws CouldNotCreateException {
        Routine routine = Routine.builder()
                .name(name)
                .description(description)
                .nextExecution(LocalDateTime.now())
                .status(RoutineStatus.PLANNED)
                .build();
        return this.routineAdapter.save(routine).orElse(null);
    }



    public Stream<Routine> getAllRoutinesByStatus(RoutineStatus status) {
        return getAllRoutines().filter(routine -> status.equals(routine.getStatus()));
    }

    public Stream<Routine> getAllRoutines() {
        return this.routineAdapter.loadAll().stream();
    }

    public void updateRoutinesToSchedule(LocalDateTime nextExecution) {
        List<Routine> routines = getAllRoutinesByStatus(RoutineStatus.PLANNED).filter(routine -> routine.getNextExecution().isBefore(nextExecution)).peek(routine -> {
            routine.setStatus(RoutineStatus.SCHEDULED);
            try {
                routineAdapter.save(routine);
            } catch (CouldNotCreateException e) {
                log.error("Could not update routine", e);
            }
        }).toList();
        routineUpdateToScheduleListener.accept(routines);
    }

    @Cacheable("routine")
    public String getUrlToData() {
        return this.routineAdapter.getUrlToData();
    }
}
