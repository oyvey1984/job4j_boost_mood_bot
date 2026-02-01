package ru.job4j;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import ru.job4j.content.Content;
import ru.job4j.telegram.BotCommandHandler;
import ru.job4j.telegram.TelegramBotService;

public class DIByDirectInjectMain   {

    @PostConstruct
    public void init() {
        System.out.println("Bean is going through @PostConstruct init.");
    }

    @PreDestroy
    public void destroy() {
        System.out.println("Bean will be destroyed via @PreDestroy.");
    }
}


