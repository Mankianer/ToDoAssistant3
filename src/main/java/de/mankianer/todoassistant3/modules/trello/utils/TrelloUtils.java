package de.mankianer.todoassistant3.modules.trello.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class TrelloUtils {
    public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
      return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date convertToDateViaInstant(LocalDateTime dateToConvert) {
      return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
}
