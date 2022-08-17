package de.mankianer.todoassistant3;

import com.julienvey.trello.domain.Card;
import lombok.NonNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class Utils {

  public static LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
    return dateToConvert.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
  }

  public static boolean isBevorOrToday(@NonNull Date date) {
    LocalDateTime localDateTime = convertToLocalDateTimeViaInstant(date);
    LocalDate localDate = localDateTime.toLocalDate();
    LocalDate today = LocalDate.now();
    return localDate.isEqual(today) || localDate.isBefore(today);
  }

  public static String CardsToMarkdownMessage(List<Card> cards) {
    String ret = "";
    for (Card card : cards) {
      ret += String.format("\n  \\*__%s__: *%s* ", card.getId(), card.getName());
    }
    return ret;
  }
}
