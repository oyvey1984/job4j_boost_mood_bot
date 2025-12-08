package ru.job4j.services;

import org.junit.jupiter.api.Test;
import ru.job4j.content.SentContent;
import ru.job4j.content.Content;
import ru.job4j.model.Mood;
import ru.job4j.model.MoodLog;
import ru.job4j.model.User;
import ru.job4j.repository.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

class ReminderServiceTest {

    @Test
    public void whenMoodGood() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };

        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));

        var moodLogRepository = new MoodLogFakeRepository();
        var userRepository = new UserFakeRepository(moodLogRepository);

        var user = new User();
        user.setChatId(100L);
        userRepository.save(user);

        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);

        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, userRepository, tgUI)
                .remindUsers();

        assertThat(result.iterator().next().getMarkup().getKeyboard()
                .iterator().next().iterator().next().getText()).isEqualTo("Good");
    }

    @Test
    public void whenUserVotedTodayThenNoReminder() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };

        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));

        var moodLogRepository = new MoodLogFakeRepository();
        var userRepository = new UserFakeRepository(moodLogRepository);

        var user = new User();
        user.setChatId(777L);
        userRepository.save(user);

        long today = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() + 1000;
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        moodLog.setCreatedAt(today);
        moodLogRepository.save(moodLog);

        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, userRepository, tgUI)
                .remindUsers();
        assertThat(result).isEmpty();
    }

    @Test
    public void whenTwoUsersOneNeedsReminder() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };

        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));

        var moodLogRepository = new MoodLogFakeRepository();
        var userRepository = new UserFakeRepository(moodLogRepository);

        var user1 = new User();
        user1.setId(1L);
        user1.setChatId(100L);
        userRepository.save(user1);

        var moodLog1 = new MoodLog();
        moodLog1.setId(1L);
        moodLog1.setUser(user1);
        long today = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() + 5000;
        moodLog1.setCreatedAt(today);
        moodLogRepository.save(moodLog1);

        var user2 = new User();
        user2.setId(2L);
        user2.setChatId(200L);
        userRepository.save(user2);

        var moodLog2 = new MoodLog();
        moodLog2.setId(2L);
        moodLog2.setUser(user2);
        long tenDaysAgo = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        moodLog2.setCreatedAt(tenDaysAgo);
        moodLogRepository.save(moodLog2);

        var startOfDay = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        var endOfDay = LocalDate.now()
                .plusDays(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;

        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, userRepository, tgUI)
                .remindUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChatId()).isEqualTo(200L);
    }

    @Test
    public void whenNoUsersThenNothingSent() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };

        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));

        var moodLogRepository = new MoodLogFakeRepository();
        var userRepository = new UserFakeRepository(moodLogRepository);

        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, userRepository, tgUI)
                .remindUsers();
        assertThat(result).isEmpty();
    }

    @Test
    public void whenUserExistsButNoVotesAtAll() {
        var result = new ArrayList<Content>();
        var sentContent = new SentContent() {
            @Override
            public void sent(Content content) {
                result.add(content);
            }
        };

        var moodRepository = new MoodFakeRepository();
        moodRepository.save(new Mood("Good", true));

        var moodLogRepository = new MoodLogFakeRepository();
        var userRepository = new UserFakeRepository(moodLogRepository);

        var user = new User();
        user.setChatId(500L);
        userRepository.save(user);
        // Не создаем MoodLog вообще

        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, userRepository, tgUI)
                .remindUsers();

        // Пользователь без голосов должен получить напоминание
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChatId()).isEqualTo(500L);
    }
}