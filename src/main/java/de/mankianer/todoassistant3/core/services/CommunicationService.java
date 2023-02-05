package de.mankianer.todoassistant3.core.services;

import de.mankianer.todoassistant3.core.adapter.CommunicationAdapter;
import de.mankianer.todoassistant3.core.models.communication.Message;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class CommunicationService {

    private List<CommunicationAdapter> communicationAdapter;

    public CommunicationService(ObjectProvider<List<CommunicationAdapter>> communicationAdapter) {
        this.communicationAdapter = communicationAdapter.getIfAvailable(Collections::emptyList);
    }

    public void sendMessage(Message message) {
        communicationAdapter.forEach(communicationAdapter -> communicationAdapter.sendMessage(message));
    }

    public void replyToMessage(Message message, String text, boolean isMarkdown) {
        Message reply = Message.builder()
                .text(text)
                .isMarkDown(isMarkdown)
                .previous(message)
                .build();
        message.setNext(reply);

        sendMessage(reply);
    }
}
