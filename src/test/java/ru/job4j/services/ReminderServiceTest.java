package ru.job4j.services;

import org.junit.jupiter.api.Test;
import ru.job4j.content.SentContent;
import ru.job4j.services.TgUI;
import ru.job4j.content.Content;
import ru.job4j.model.Mood;
import ru.job4j.model.MoodLog;
import ru.job4j.model.User;
import ru.job4j.repository.MoodFakeRepository;
import ru.job4j.repository.MoodLogFakeRepository;

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
        var user = new User();
        user.setChatId(100);
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        var yesterday = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() - 1;
        moodLog.setCreatedAt(yesterday);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, moodLogRepository, tgUI)
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
        var user = new User();
        user.setChatId(777);
        long today = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() + 1000;
        var moodLog = new MoodLog();
        moodLog.setUser(user);
        moodLog.setCreatedAt(today);
        moodLogRepository.save(moodLog);
        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, moodLogRepository, tgUI)
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
        var user1 = new User();
        user1.setId(1L);
        user1.setChatId(100);
        var moodLog1 = new MoodLog();
        moodLog1.setUser(user1);
        long today = LocalDate.now()
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli() + 5000;
        moodLog1.setCreatedAt(today);
        moodLogRepository.save(moodLog1);

        var user2 = new User();
        user2.setId(2L);
        user2.setChatId(200);
        var moodLog2 = new MoodLog();
        moodLog2.setUser(user2);
        long tenDaysAgo = LocalDate.now()
                .minusDays(10)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
        moodLog2.setCreatedAt(tenDaysAgo);
        moodLogRepository.save(moodLog2);

        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getChatId()).isEqualTo(200);
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
        var tgUI = new TgUI(moodRepository);
        new ReminderService(sentContent, moodLogRepository, tgUI)
                .remindUsers();
        assertThat(result).isEmpty();
    }
}
