package de.mankianer.todoassistant3;

import de.mankianer.mankianerstelegramspringstarter.TelegramService;
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
        System.out.println("Hello World!");
    }

    @GetMapping
    public String hello() {
        return "Hello World!";
    }
}
