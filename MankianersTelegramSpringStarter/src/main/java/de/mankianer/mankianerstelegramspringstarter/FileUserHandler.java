package de.mankianer.mankianerstelegramspringstarter;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

@Log4j2
@Component
public class FileUserHandler implements UserHandler {

  @Value("${telegram.user.file:.cache/telegram-user.cache}")
  private String pathToRegisterUserFile;

  private Map<String, String> registerUserChatIdMap = new HashMap<>();
  private File registerUserFile;
  private Path registerUserFilePath;

  @PostConstruct
  public void init() {
    registerUserFilePath = Paths.get(pathToRegisterUserFile);
    registerUserFile = registerUserFilePath.toFile();
    try {
      if (registerUserFile.exists()) {
        loadFromFile();
      } else {
        registerUserFile.getParentFile().mkdirs();
        registerUserFile.createNewFile();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void loadFromFile() throws IOException {
    Files.readAllLines(registerUserFilePath)
        .forEach(
            line -> {
              String[] split = line.split(":");
              if (split.length == 2) {
                registerUserChatIdMap.put(split[0], split[1]);
              }
            });
  }

  public void saveToFile() throws IOException {
    List<String> lines =
        registerUserChatIdMap.entrySet().stream()
            .map(entry -> (entry.getKey() + ":" + entry.getValue() + "\n"))
            .toList();
    Files.write(registerUserFilePath, lines);
  }

  @Override
  public void registerUser(Message message) {
    registerUserChatIdMap.put(message.getFrom().getUserName(), message.getChatId().toString());
    try {
      saveToFile();
    } catch (IOException e) {
      log.error("Could not save user to file", e);
    }
  }

  @Override
  public void unregisterUser(User user) {
    registerUserChatIdMap.remove(user.getUserName());
    try {
      saveToFile();
    } catch (IOException e) {
      log.error("Could not save user to file", e);
    }
  }

  @Override
  public boolean isUserRegistered(User user) {
    return registerUserChatIdMap.containsKey(user.getUserName());
  }

  @Override
  public void forEach(BiConsumer<? super String, ? super String> consumer) {
    registerUserChatIdMap.forEach(consumer);
  }
}
