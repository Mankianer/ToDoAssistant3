package de.mankianer.todoassistant3;

import lombok.NonNull;

import java.time.LocalDateTime;

public class Utils {

  public static boolean isBevorOrToday(@NonNull LocalDateTime localDate) {
    return localDate.isEqual(LocalDateTime.now()) || localDate.isBefore(LocalDateTime.now());
  }
}
