package de.mankianer.todoassistant3;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = "de.mankianer")
public class ToDoAssistant3Application {

    public static void main(String[] args) {
        SpringApplication.run(ToDoAssistant3Application.class, args);
    }

}
