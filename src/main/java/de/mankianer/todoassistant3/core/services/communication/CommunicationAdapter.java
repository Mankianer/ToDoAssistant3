package de.mankianer.todoassistant3.core.services.communication;

import de.mankianer.todoassistant3.core.models.message.Message;

public interface CommunicationAdapter {

    void sendMessage(Message message);

}
