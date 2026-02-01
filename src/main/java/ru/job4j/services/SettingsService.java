package ru.job4j.services;

import org.springframework.stereotype.Service;
import ru.job4j.content.Content;
import ru.job4j.model.User;
import ru.job4j.repository.UserRepository;

@Service
public class SettingsService {

    private final UserRepository userRepository;
    private final TgUI tgUI;

    public SettingsService(UserRepository userRepository, TgUI tgUI) {
        this.userRepository = userRepository;
        this.tgUI = tgUI;
    }

    public Content showSettings(User user) {
        Content content = new Content(user.getChatId());
        content.setText("⚙️ Настройки:");
        content.setMarkup(tgUI.buildSettingsButtons(user));
        return content;
    }

    public Content toggleAdvice(User user) {
        user.setAdviceEnabled(!user.isAdviceEnabled());
        userRepository.save(user);
        return showSettings(user);
    }

    public Content toggleReminder(User user) {
        user.setRemindersEnabled(!user.isRemindersEnabled());
        userRepository.save(user);
        return showSettings(user);
    }
}
