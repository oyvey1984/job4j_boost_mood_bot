package ru.job4j.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.job4j.model.Mood;
import ru.job4j.repository.MoodFakeRepository;
import ru.job4j.repository.MoodRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {TgUI.class, MoodFakeRepository.class})
class TgUITest {

    @Autowired
    private TgUI ui;

    @Autowired
    @Qualifier("moodFakeRepository")
    private MoodRepository moodRepository;

    @Test
    public void whenBtnGood() {
        assertThat(moodRepository).isNotNull();
    }

    @Test
    void whenBuildButtonsThenButtonsCreated() {
        moodRepository.save(new Mood("Good", true));
        moodRepository.save(new Mood("Bad", false));
        assertThat(ui).isNotNull();
        assertThat(moodRepository).isNotNull();
        InlineKeyboardMarkup markup = ui.buildButtons();
        assertThat(markup.getKeyboard()).isNotEmpty();
        assertThat(markup.getKeyboard()).hasSize(moodRepository.findAll().size());
    }
}