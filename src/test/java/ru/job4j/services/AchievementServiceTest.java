package ru.job4j.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.job4j.content.FakeSentContent;
import ru.job4j.events.UserEvent;
import ru.job4j.model.*;
import ru.job4j.repository.AchievementFakeRepository;
import ru.job4j.repository.AwardFakeRepository;
import ru.job4j.repository.MoodLogFakeRepository;

import java.time.LocalDate;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;

class AchievementServiceTest {

    private MoodLogFakeRepository moodLogRepository;
    private AwardFakeRepository awardRepository;
    private AchievementFakeRepository achievementRepository;
    private FakeSentContent sentContent;
    private AchievementService service;
    private User user;

    @BeforeEach
    void init() {
        moodLogRepository = new MoodLogFakeRepository();
        awardRepository = new AwardFakeRepository();
        achievementRepository = new AchievementFakeRepository();
        sentContent = new FakeSentContent();

        service = new AchievementService(
                moodLogRepository,
                awardRepository,
                achievementRepository,
                sentContent
        );

        user = new User();
        user.setId(1L);
        user.setClientId(100L);
        user.setChatId(555L);
    }

    private long millisOfDay(LocalDate date) {
        return date.atStartOfDay(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli();
    }

    @Test
    void whenNoLogsThenNoAchievements() {
        awardRepository.save(new Award("First", "desc", 3));

        service.onApplicationEvent(new UserEvent(this, user));

        assertThat(achievementRepository.findAll()).isEmpty();
        assertThat(sentContent.getSentMessages()).isEmpty();
    }

    @Test
    void whenStreakEnoughThenAchievementCreated() {
        awardRepository.save(new Award("3 days", "desc", 3));

        for (int i = 0; i < 3; i++) {
            Mood mood = new Mood();
            mood.setGood(true);

            MoodLog log = new MoodLog();
            log.setId((long) i);
            log.setUser(user);
            log.setMood(mood);
            log.setCreatedAt(millisOfDay(LocalDate.now().minusDays(i)));

            moodLogRepository.save(log);
        }

        service.onApplicationEvent(new UserEvent(this, user));

        assertThat(achievementRepository.findAll()).hasSize(1);
        assertThat(sentContent.getSentMessages()).hasSize(1);
    }

    @Test
    void whenAchievementAlreadyReceivedThenNotSentTwice() {
        Award award = new Award("3 days", "desc", 3);
        awardRepository.save(award);

        for (int i = 0; i < 3; i++) {
            Mood mood = new Mood();
            mood.setGood(true);

            MoodLog log = new MoodLog();
            log.setId((long) i);
            log.setUser(user);
            log.setMood(mood);
            log.setCreatedAt(millisOfDay(LocalDate.now().minusDays(i)));

            moodLogRepository.save(log);
        }

        Achievement achievement = new Achievement();
        achievement.setAward(award);
        achievement.setUser(user);

        achievementRepository.save(achievement);

        service.onApplicationEvent(new UserEvent(this, user));

        assertThat(achievementRepository.findAll()).hasSize(1);
        assertThat(sentContent.getSentMessages()).isEmpty();
    }
}
