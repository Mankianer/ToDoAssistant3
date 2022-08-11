package de.mankianer.todoassistet3;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class HelloWorld {

    @PostConstruct
    public void init() {
        System.out.println("Hello World!");
    }
}
