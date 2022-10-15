package de.mankianer.todoassistant3.models.todo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Builder
@Data
public class ToDo {

    private String id;
    private String name;
    private String description;
    private ToDoStatus status;
    private LocalDateTime dueDate;
}
