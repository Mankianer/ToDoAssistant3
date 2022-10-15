package de.mankianer.todoassistant3.services.communication;

import de.mankianer.todoassistant3.models.message.Message;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

@Service
public class CommunicationService {

    private List<CommunicationAdapter> communicationAdapter;

    public CommunicationService(ObjectProvider<List<CommunicationAdapter>> communicationAdapter) {
        this.communicationAdapter = communicationAdapter.getIfAvailable(Collections::emptyList);
    }

    public void sendMessage(Message message) {
        communicationAdapter.forEach(communicationAdapter -> communicationAdapter.sendMessage(message));
    }

    public void registerOnIncomingMessage(Consumer<Message> onIncomingMessage) {
        communicationAdapter.forEach(communicationAdapter -> communicationAdapter.registerOnIncomingMessage(onIncomingMessage));
    }

    public void addCommunicationAdapter(CommunicationAdapter communicationAdapter) {
        this.communicationAdapter.add(communicationAdapter);
    }
}
