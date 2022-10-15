package de.mankianer.todoassistant3.model.message;

import lombok.Data;

import java.util.List;

@Data
public class MessageThread {
    private String id;
    private List<Message> messages;
}
