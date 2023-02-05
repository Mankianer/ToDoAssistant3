package de.mankianer.todoassistant3.modules.telegram.commands.routines;

import de.mankianer.todoassistant3.core.models.routines.Routine;

import java.util.List;

public class TelegramRoutineUtils {

    private TelegramRoutineUtils() {
    }

    public static String getRoutineTable(List<Routine> routines) {
        StringBuilder table = new StringBuilder();
        for (int i = 0; i < routines.size(); i++) {
            table.append(getRoutineTableRow(i, routines.get(i)));
        }


        return """
            ```
            |NR|Name                |Zeit/Datum|
            |--|--------------------|----------|
            %s
            ```
            """.formatted(table.toString());
    }

    public static String getRoutineTableRow(int pos, Routine routine) {
        String pattern = switch (routine.getStatus()) {
            case SCHEDULED -> "|%2d|%-20s|%3$td:%3$tm-%3$ta |%n";
            case PLANNED -> "|%2d|%-20s|%3$tH.%3$tM-%3$ta |%n";
            default -> "|%2d|%-20s|%3$tH.%3$tM-%3$ta |%n";
        };
        return pattern.formatted(pos, routine.getName(), routine.getNextExecution());
    }
}