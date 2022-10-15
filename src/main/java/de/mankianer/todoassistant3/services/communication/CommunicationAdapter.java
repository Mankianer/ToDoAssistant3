package de.mankianer.todoassistant3.services.communication;

import de.mankianer.todoassistant3.models.message.Message;

public interface CommunicationAdapter {

    void sendMessage(Message message);

}
