package ru.job4j.telegram;

import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.job4j.content.Content;
import ru.job4j.content.SentContent;

@Service
@Conditional(OnFakeCondition.class)
public class TelegramBotFakeService extends TelegramLongPollingBot implements SentContent {
    @Override
    public void onUpdateReceived(Update update) {
    }

    @Override
    public String getBotUsername() {
        return "";
    }

    @Override
    public void sent(Content content) {
        System.out.println(content.getText());
    }
}
