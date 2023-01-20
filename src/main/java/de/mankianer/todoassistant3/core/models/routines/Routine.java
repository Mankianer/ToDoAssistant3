package de.mankianer.todoassistant3.core.models.routines;

import lombok.Builder;
import lombok.Data;
import org.springframework.scheduling.support.CronExpression;

import java.time.LocalDateTime;

@Data
@Builder
public class Routine {

      private String id;
      private String name;
      private String description;
      private String url;
      private RoutineStatus status;

      private LocalDateTime nextExecution;
      private CronExpression cron;
}