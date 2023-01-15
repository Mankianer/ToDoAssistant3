package de.mankianer.todoassistant3.modules.trello.routines;

import de.mankianer.todoassistant3.core.exceptions.CouldNotCreateException;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.services.routines.RoutineAdapter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TrelloRoutineService implements RoutineAdapter {

    @Override
    public Optional<Routine> load(String id) {
        return Optional.empty();
    }

    @Override
    public Routine save(Routine routine) throws CouldNotCreateException {
        return null;
    }

    @Override
    public Routine delete(String id) {
        return null;
    }

    @Override
    public List<Routine> loadAll() {
        return List.of();
    }
}
