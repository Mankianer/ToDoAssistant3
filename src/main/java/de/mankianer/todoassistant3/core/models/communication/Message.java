package de.mankianer.todoassistant3.core.models.communication;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Message<T> {

    private String text;
    private boolean isMarkDown;
    private T context;
    private Message next, previous;

    public static Message of(String text){
        return Message.builder().text(text).build();
    }

    public static Message ofMd(String text){
        return Message.builder().text(text).isMarkDown(true).build();
    }
}
