package ru.job4j.services;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.job4j.exception.RepositoryAccessException;
import ru.job4j.model.Mood;
import ru.job4j.model.User;
import ru.job4j.repository.MoodRepository;

import java.util.ArrayList;
import java.util.List;

@Component
public class TgUI {
    private final MoodRepository moodRepository;

    public TgUI(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    public InlineKeyboardMarkup buildButtons() {
        var inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> keyboard = new ArrayList<>();
        List<Mood> moods;
        try {
            moods = moodRepository.findAll();
        } catch (Exception e) {
            throw new RepositoryAccessException("Failed to load moods for Telegram UI", e);
        }
        for (var mood : moods) {
            keyboard.add(List.of(createBtn(mood.getText(), mood.getId())));
        }
        inlineKeyboardMarkup.setKeyboard(keyboard);
        return inlineKeyboardMarkup;
    }

    private InlineKeyboardButton createBtn(String name, Long moodId) {
        var inline = new InlineKeyboardButton();
        inline.setText(name);
        inline.setCallbackData("MOOD:" + moodId);
        return inline;
    }

    public InlineKeyboardMarkup buildSettingsButtons(User user) {
        InlineKeyboardButton reminderBtn = new InlineKeyboardButton();
        reminderBtn.setText("🔔 Напоминания: " + (user.isRemindersEnabled() ? "ВКЛ" : "ВЫКЛ"));
        reminderBtn.setCallbackData(SettingsCallback.TOGGLE_REMINDER.name());

        InlineKeyboardButton adviceBtn = new InlineKeyboardButton();
        adviceBtn.setText("💡 Советы: " + (user.isAdviceEnabled() ? "ВКЛ" : "ВЫКЛ"));
        adviceBtn.setCallbackData(SettingsCallback.TOGGLE_ADVICE.name());

        return new InlineKeyboardMarkup(
                List.of(
                        List.of(reminderBtn),
                        List.of(adviceBtn)
                )
        );
    }

}
