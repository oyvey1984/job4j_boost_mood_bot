package ru.job4j.telegram;

import org.springframework.stereotype.Service;
import ru.job4j.content.Content;

@Service
public class BotCommandHandler {
    void receive(Content content) {
        System.out.println(content);
    }
}
