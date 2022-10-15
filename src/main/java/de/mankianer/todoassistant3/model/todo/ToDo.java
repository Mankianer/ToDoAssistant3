package de.mankianer.todoassistant3.model.todo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ToDo {

    private String id;
    private String name;
    private String description;
    private String status;
    private LocalDateTime dueDate;
}
