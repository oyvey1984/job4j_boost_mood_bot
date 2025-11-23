package ru.job4j.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.job4j.model.User;
import ru.job4j.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TgRemoteService extends TelegramLongPollingBot {

    private final String botName;
    private final String botToken;
    private final UserRepository userRepository;
    private final TgUI tgUI;
    private final MoodService moodService;

    public TgRemoteService(@Value("${telegram.bot.name}") String botName,
                           @Value("${telegram.bot.token}") String botToken,
                           UserRepository userRepository, TgUI tgUI, MoodService moodService) {
        this.botName = botName;
        this.botToken = botToken;
        this.userRepository = userRepository;
        this.tgUI = tgUI;
        this.moodService = moodService;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            var data = update.getCallbackQuery().getData();
            var chatId = update.getCallbackQuery().getMessage().getChatId();

            long moodId;
            try {
                moodId = Long.parseLong(data);
            } catch (NumberFormatException e) {
                send(new SendMessage(String.valueOf(chatId), "Ошибка выбора настроения."));
                return;
            }

            var userOpt = userRepository.findByChatId(chatId);
            if (userOpt.isEmpty()) {
                send(new SendMessage(String.valueOf(chatId), "Пользователь не найден."));
                return;
            }
            var user = userOpt.get();
            var content = moodService.chooseMood(user, moodId);
            send(new SendMessage(String.valueOf(chatId), content.getText()));
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = update.getMessage();
            if ("/start".equals(message.getText())) {
                long chatId = message.getChatId();
                var user = new User();
                user.setClientId(message.getFrom().getId());
                user.setChatId(chatId);
                userRepository.add(user);
                send(sendButtons(chatId));
            }
        }
    }

    private void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public SendMessage sendButtons(long chatId) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Как настроение сегодня?");
        message.setReplyMarkup(tgUI.buildButtons());
        return message;
    }

    InlineKeyboardButton createBtn(String name, String data) {
        var inline = new InlineKeyboardButton();
        inline.setText(name);
        inline.setCallbackData(data);
        return inline;
    }
}