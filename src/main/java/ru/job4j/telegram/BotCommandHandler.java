package ru.job4j.telegram;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.job4j.content.Content;
import ru.job4j.model.User;
import ru.job4j.repository.UserRepository;
import ru.job4j.services.*;

import java.util.Optional;

@Service
public class BotCommandHandler {
    private final UserRepository userRepository;
    private final MoodService moodService;
    private final TgUI tgUI;
    private final AdviceService adviceService;
    private final SettingsService settingsService;

    public BotCommandHandler(UserRepository userRepository,
                             MoodService moodService,
                             TgUI tgUI,
                             AdviceService adviceService, SettingsService settingsService) {
        this.userRepository = userRepository;
        this.moodService = moodService;
        this.tgUI = tgUI;
        this.adviceService = adviceService;
        this.settingsService = settingsService;
    }

    Optional<Content> commands(Message message) {
        String txt = message.getText();
        long chatId = message.getChatId();
        Long clientId = message.getFrom().getId();

        return switch (txt) {
            case "/start" -> handleStartCommand(chatId, clientId);
            case "/week_mood_log" -> moodService.weekMoodLogCommand(chatId, clientId);
            case "/month_mood_log" -> moodService.monthMoodLogCommand(chatId, clientId);
            case "/award" -> moodService.awards(chatId, clientId);
            case "/daily_advice" -> {
                User user = userRepository.findByClientId(clientId)
                        .orElseThrow(() -> new IllegalStateException("User not found"));
                yield adviceService.personalAdvice(user.getId());
            }
            case "/settings" -> {
                User user = userRepository.findByClientId(clientId)
                        .orElseThrow(() -> new IllegalStateException("User not found"));
                yield Optional.of(settingsService.showSettings(user));
            }
            default -> Optional.empty();
        };
    }

    Optional<Content> handleCallback(CallbackQuery callback) {
        String data = callback.getData();
        User user = userRepository.findByClientId(callback.getFrom().getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        if (data.startsWith("MOOD:")) {
            Long moodId = Long.valueOf(data.substring("MOOD:".length()));
            return Optional.of(moodService.chooseMood(user, moodId));
        }

        try {
            SettingsCallback settingsCallback = SettingsCallback.valueOf(data);
            return switch (settingsCallback) {
                case TOGGLE_ADVICE -> Optional.of(settingsService.toggleAdvice(user));
                case TOGGLE_REMINDER -> Optional.of(settingsService.toggleReminder(user));
                case SETTINGS -> Optional.of(settingsService.showSettings(user));
            };
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    private Optional<Content> handleStartCommand(long chatId, Long clientId) {
        var user = userRepository.findByChatId(chatId).
                orElseGet(() -> {
                    var u = new User();
                    u.setChatId(chatId);
                    u.setClientId(clientId);
                    return userRepository.save(u);
                });
        var content = new Content(user.getChatId());
        content.setText("Добро пожаловать! Я бот для отслеживания настроения. "
                + "Я буду спрашивать вас о настроении каждый день. "
                + "Выберите ваше текущее настроение:");
        content.setMarkup(tgUI.buildButtons());
        return Optional.of(content);
    }
}
