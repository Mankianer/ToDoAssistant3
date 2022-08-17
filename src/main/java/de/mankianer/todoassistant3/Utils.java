package de.mankianer.todoassistant3;

import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Utils {

  public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static boolean isBevorOrToday(@NonNull Date date) {
    LocalDateTime localDateTime = convertToLocalDateTimeViaInstant(date);
    LocalDate localDate = localDateTime.toLocalDate();
    LocalDate today = LocalDate.now();
    return localDate.isEqual(today) || localDate.isAfter(today);
  }
}
