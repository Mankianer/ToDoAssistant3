package de.mankianer.todoassistet3;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RequestMapping("/hello")
@RestController
public class HelloWorld {

    @PostConstruct
    public void init() {
        System.out.println("Hello World!");
    }

    @GetMapping
    public String hello() {
        return "Hello World!";
    }
}
