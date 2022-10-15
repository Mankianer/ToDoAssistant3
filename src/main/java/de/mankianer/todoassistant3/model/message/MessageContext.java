package de.mankianer.todoassistant3.model.message;

import lombok.Data;

@Data
public class MessageContext<T> {
    private T context;
}
