package de.mankianer.todoassistant3.models.message;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class Message<T> {

    private UUID id;
    private String text;
    private boolean isMarkDown;
    private MessageContext<T> context;
    private Message next, previous;

    public UUID getId() {
        if(id == null) {
            id = UUID.randomUUID();
        }
        return id;
    }
}
