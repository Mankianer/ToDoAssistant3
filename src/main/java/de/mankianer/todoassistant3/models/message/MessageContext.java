package de.mankianer.todoassistant3.models.message;

import lombok.Data;

@Data
public class MessageContext<T> {
    private T context;
}
