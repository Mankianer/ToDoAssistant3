package de.mankianer.todoassistant3.modules.telegram.commands.routines;

import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversation;
import de.mankianer.mankianerstelegramspringstarter.commands.models.TelegramConversationInterface;
import de.mankianer.todoassistant3.core.models.routines.Routine;
import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class EditRoutineListConversation implements TelegramConversationInterface {
    private static String EDIT = "Edit ";

    @Setter
    @Getter
    private Long chatId;

    private final List<Routine> routines;
    private String EDIT;

    public EditRoutineListConversation(List<Routine> routines) {
        this.routines = routines;
    }

    public SendMessage getMessage() {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdownV2(true);
        sendMessage.setText("Welche Routine möchtest du bearbeiten?\n" + TelegramRoutineUtils.getRoutineTable(routines));
        return sendMessage;
    }

    public void enterConversation() {
        // TODO Auto-generated method stub
    }

    @Override
    public TelegramConversationInterface onAnswer(String answer) {
        if(answer.startsWith(EDIT)) {
            String substring = answer.substring(EDIT.length());
            try {
                int routineNr = Integer.parseInt(substring);
                TelegramConversation.builder(TelegramRoutineUtils.getRoutineTableRow(routineNr, routines.get(routineNr)) + "\nVerschieben auf?", null)
                        .on("Morgen").then("Auf Morgen verschoben").
                        .build();

            } catch (NumberFormatException e) {
                return TelegramConversation.unknownAnswerConversation(answer, this);
            }
        }
        return TelegramConversation.unknownAnswerConversation(answer, this);
    }

    @Override
    public void onAbort(AbortReason reason) {

    }

    @Override
    public Collection<String> getOptions() {
        var options = new ArrayList<String>();
        for(int i = 0; i < routines.size(); i++) {
          options.add(EDIT + i);
        }
        return options;
    }


}
