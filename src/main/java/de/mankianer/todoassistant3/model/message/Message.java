package de.mankianer.todoassistant3.model.message;

import de.mankianer.todoassistant3.model.user.User;
import lombok.Data;

@Data
public class Message<T> {
    private String id;
    private String message;
    private MessageContext<T> context;
    private MessageThread thread;
    private User user;
}
