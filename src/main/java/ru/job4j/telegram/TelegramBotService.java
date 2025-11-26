package ru.job4j.telegram;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendAudio;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.content.Content;
import ru.job4j.content.SentContent;
import ru.job4j.content.SentContentException;

@Service
public class TelegramBotService extends TelegramLongPollingBot implements SentContent {
    private final BotCommandHandler handler;
    private final String botName;

    public TelegramBotService(@Value("${telegram.bot.name}") String botName,
                              @Value("${telegram.bot.token}") String botToken,
                              BotCommandHandler handler) {
        super(botToken);
        this.handler = handler;
        this.botName = botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            handler.handleCallback(update.getCallbackQuery())
                    .ifPresent(this::sent);
        } else if (update.hasMessage() && update.getMessage().getText() != null) {
            handler.commands(update.getMessage())
                    .ifPresent(this::sent);
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void sent(Content content) {
        long chatId = content.getChatId();

        try {
            if (content.getAudio() != null) {
                SendAudio audio = new SendAudio();
                audio.setChatId(chatId);
                audio.setAudio(content.getAudio());
                audio.setCaption(content.getText());
                execute(audio);
                return;
            }

            if (content.getPhoto() != null) {
                SendPhoto photo = new SendPhoto();
                photo.setChatId(chatId);
                photo.setPhoto(content.getPhoto());
                photo.setCaption(content.getText());
                execute(photo);
                return;
            }

            if (content.getText() != null) {
                SendMessage message = new SendMessage();
                message.setChatId(chatId);
                message.setText(content.getText());
                if (content.getMarkup() != null) {
                    message.setReplyMarkup(content.getMarkup());
                }
                execute(message);
            }

        } catch (TelegramApiException e) {
            throw new SentContentException("Ошибка отправки контента", e);
        }
    }
}