package de.mankianer.todoassistant3.services.communication;

import de.mankianer.todoassistant3.model.message.Message;

import java.util.function.Consumer;

public interface CommunicationAdapter {

    void sendMessage(Message message);
    void registerOnIncomingMessage(Consumer<Message> onIncomingMessage);
}
