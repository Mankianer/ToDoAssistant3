package de.mankianer.todoassistant3;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
import de.mankianer.todoassistant3.controller.TrelloController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RequestMapping("/hello")
@RestController
public class HelloWorld {

    private final TelegramService telegramService;


    public HelloWorld(TelegramService telegramService) {
        this.telegramService = telegramService;
    telegramService.registerMessageHandlerFunction(
        message -> {
          message.reply("Hallo World!");
          return true;
        });
    }


    @PostConstruct
    public void init() {
        telegramService.registerCommand(new HelloTelegramCommand(telegramService));
        System.out.println("Hello World!");
    }

    @GetMapping
    public String hello() {
        return "Hello World!";
    }
}
