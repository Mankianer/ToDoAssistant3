package de.mankianer.todoassistant3.core.models.todos;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ToDo {

    private String id;
    private String name;
    private String description;
    private String url;
    private ToDoStatus status;
    private LocalDateTime dueDate;
}
