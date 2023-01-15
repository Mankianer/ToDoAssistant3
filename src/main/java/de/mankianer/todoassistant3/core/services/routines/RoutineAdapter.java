package de.mankianer.todoassistant3.core.services.routines;

import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.routines.Routine;

import java.util.List;
import java.util.Optional;

public interface RoutineAdapter {
    Optional<Routine> load(String id);
    Routine save(Routine routine) throws CouldNotCreateException;
    Routine delete(String id);

    List<Routine> loadAll();
}
