package de.mankianer.todoassistant3.core.controllers;

import de.mankianer.todoassistant3.core.services.communication.CommunicationService;
import de.mankianer.todoassistant3.core.services.routines.RoutineService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Log4j2
@Service
public class RoutineController {

    private final RoutineService routineService;
    private CommunicationService communicationService;
    private final CronExpression updateScheduledRoutinesCronExpression;

    public RoutineController(@Autowired RoutineService routineService, @Autowired CommunicationService communicationService,
                             @Value("${routine.updateSchedule.cron.expression}") String updateScheduledRoutinesCronExpressionStr) {
        this.routineService = routineService;
        this.communicationService = communicationService;
        updateScheduledRoutinesCronExpression = CronExpression.parse(updateScheduledRoutinesCronExpressionStr);
    }

    @Scheduled(cron = "${routine.updateSchedule.cron.expression}")
    public void updateRoutinesToSchedule() {
        log.trace("Update routines to schedule");
        routineService.updateRoutinesToSchedule(updateScheduledRoutinesCronExpression.next(LocalDateTime.now()));
    }

}
