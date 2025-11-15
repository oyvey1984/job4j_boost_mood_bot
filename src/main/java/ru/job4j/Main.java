package ru.job4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import ru.job4j.config.AppConfig;
import ru.job4j.content.Content;
import ru.job4j.telegram.TelegramBotService;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner checkEnv(ApplicationContext ctx) {
        return args -> {
            System.out.println(ctx.getEnvironment().getProperty("telegram.bot.name"));
        };
    }
}