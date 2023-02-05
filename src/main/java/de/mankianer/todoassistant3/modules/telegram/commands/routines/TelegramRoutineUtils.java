package de.mankianer.todoassistant3.modules.telegram.commands.routines;

import de.mankianer.todoassistant3.core.models.routines.Routine;
import de.mankianer.todoassistant3.core.models.routines.RoutineStatus;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class TelegramRoutineUtils {

    private TelegramRoutineUtils() {
    }

    public static String getRoutineTable(List<Routine> routines) {
        StringBuilder table = new StringBuilder();
        for (int i = 0; i < routines.size(); i++) {
            table.append(getRoutineTableRow(i, routines.get(i)));
        }

        Set<RoutineStatus> allStatus = getAllStatus(routines);
        String lastHeader = "Datum";
        if (allStatus.contains(RoutineStatus.SCHEDULED)) {
            if(allStatus.size() > 1) {
                lastHeader = "Zeit/Datum";
            } else {
                lastHeader = "Zeit";
            }
        }


        return """
            ```
            |NR|Name                |%-10s|
            |--|--------------------|----------|
            %s
            ```
            """.formatted(lastHeader, table.toString());
    }

    public static Set<RoutineStatus> getAllStatus(List<Routine> routines) {
        return routines.stream().map(Routine::getStatus).collect(Collectors.toSet());
    }

    public static String getRoutineTableRow(int pos, Routine routine) {
        String pattern = switch (routine.getStatus()) {
            case PLANNED -> "|%2d|%-20s|%3$td.%3$tm-%3$ta |%n";
            case SCHEDULED -> "|%2d|%-20s|%3$tH:%3$tM-%3$ta |%n";
            default -> "|%2d|%-20s|%3$td.%3$tm-%3$ta |%n";
        };
        return pattern.formatted(pos, routine.getName(), routine.getNextExecution());
    }
}