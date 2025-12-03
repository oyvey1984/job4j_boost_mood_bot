package ru.job4j.services;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import ru.job4j.content.Content;
import ru.job4j.content.SentContent;
import ru.job4j.events.UserEvent;
import ru.job4j.model.Achievement;
import ru.job4j.model.MoodLog;
import ru.job4j.model.User;
import ru.job4j.repository.AchievementRepository;
import ru.job4j.repository.AwardRepository;
import ru.job4j.repository.MoodLogRepository;
import ru.job4j.model.Award;

import java.time.Instant;
import java.time.ZoneId;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AchievementService implements ApplicationListener<UserEvent> {

    private final MoodLogRepository moodLogRepository;
    private final AwardRepository awardRepository;
    private final AchievementRepository achievementRepository;
    private final SentContent sentContent;

    public AchievementService(MoodLogRepository moodLogRepository,
                              AwardRepository awardRepository,
                              AchievementRepository achievementRepository,
                              SentContent sentContent) {
        this.moodLogRepository = moodLogRepository;
        this.awardRepository = awardRepository;
        this.achievementRepository = achievementRepository;
        this.sentContent = sentContent;
    }

    @Transactional
    @Override
    public void onApplicationEvent(UserEvent event) {
        User user = event.getUser();
        long now = System.currentTimeMillis();
        long sixtyDaysAgo = now - 60L * 24 * 60 * 60 * 1000;
        List<MoodLog> logs = moodLogRepository
                .findByUserClientIdAndCreatedAtBetween(
                        user.getClientId(),
                        sixtyDaysAgo,
                        now
                );
        long streak = calculateStreak(logs);
        List<Award> awards = awardRepository.findAll();
        for (Award award : awards) {
            if (streak >= award.getDays()) {
                boolean alreadyReceived = achievementRepository
                        .existsByUserClientIdAndAwardId(user.getClientId(), award.getId());
                if (!alreadyReceived) {
                    Achievement achievement = new Achievement();
                    achievement.setAward(award);
                    achievement.setUser(user);
                    achievement.setCreateAt(now);
                    achievementRepository.save(achievement);
                    sendAchievementNotification(user, award);
                }
            }
        }
    }

    private long calculateStreak(List<MoodLog> logs) {
        if (logs.isEmpty()) {
            return 0;
        }

        Set<LocalDate> goodDays = logs.stream()
                .filter(log -> log.getMood().isGood())
                .map(log -> Instant.ofEpochMilli(log.getCreatedAt())
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate())
                .collect(Collectors.toCollection(TreeSet::new));

        LocalDate lastDate = goodDays.stream()
                .max(LocalDate::compareTo)
                .orElse(LocalDate.MIN);

        long streak = 0;
        LocalDate checkDate = lastDate;

        while (goodDays.contains(checkDate)) {
            streak++;
            checkDate = checkDate.minusDays(1);
        }

        return streak;
    }

    private void sendAchievementNotification(User user, Award award) {
        Content content = new Content(user.getChatId());
        content.setText(
                "🎉 Поздравляем! Вы получили достижение:\n\n"
                        + "🏆 *" + award.getTitle() + "*\n\n"
                        + award.getDescription()
        );
        sentContent.sent(content);
    }
}
