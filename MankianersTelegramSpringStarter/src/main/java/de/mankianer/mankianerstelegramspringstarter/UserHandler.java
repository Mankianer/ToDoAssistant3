package de.mankianer.mankianerstelegramspringstarter;

import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.function.BiConsumer;

public interface UserHandler {
    void registerUser(Update update);

    void unregisterUser(User user);

    boolean isUserRegistered(User user);

    void forEach(BiConsumer<? super String, ? super String> consumer);
}
