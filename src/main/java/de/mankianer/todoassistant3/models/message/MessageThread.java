package de.mankianer.todoassistant3.models.message;

import lombok.Data;

import java.util.List;

@Data
public class MessageThread {
    private String id;
    private List<Message> messages;
}
