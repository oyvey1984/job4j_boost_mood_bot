package ru.job4j.services;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import ru.job4j.content.Content;
import ru.job4j.events.UserEvent;
import ru.job4j.model.*;
import ru.job4j.repository.AchievementRepository;
import ru.job4j.repository.MoodLogRepository;
import ru.job4j.repository.MoodRepository;
import ru.job4j.repository.UserRepository;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class MoodService {
    private final MoodLogRepository moodLogRepository;
    private final MoodRepository moodRepository;
    private final RecommendationEngine recommendationEngine;
    private final UserRepository userRepository;
    private final AchievementRepository achievementRepository;
    private final DateTimeFormatter formatter = DateTimeFormatter
            .ofPattern("dd-MM-yyyy HH:mm")
            .withZone(ZoneId.systemDefault());
    private final ApplicationEventPublisher publisher;

    public MoodService(MoodLogRepository moodLogRepository, MoodRepository moodRepository,
                       RecommendationEngine recommendationEngine,
                       UserRepository userRepository,
                       AchievementRepository achievementRepository, ApplicationEventPublisher publisher) {
        this.moodLogRepository = moodLogRepository;
        this.moodRepository = moodRepository;
        this.recommendationEngine = recommendationEngine;
        this.userRepository = userRepository;
        this.achievementRepository = achievementRepository;
        this.publisher = publisher;
    }

    public Content chooseMood(User user, Long moodId) {
        Mood mood = moodRepository.findById(moodId)
                .orElseThrow(() -> new IllegalArgumentException("Mood not found: " + moodId));
        MoodLog log = new MoodLog();
        log.setUser(user);
        log.setMood(mood);
        log.setCreatedAt(System.currentTimeMillis());
        moodLogRepository.save(log);
        publisher.publishEvent(new UserEvent(this, user));
        return recommendationEngine.recommendFor(user.getChatId(), moodId);
    }

    public Optional<Content> weekMoodLogCommand(long chatId, Long clientId) {
        long now = System.currentTimeMillis();
        long weekAgo = now - 7L * 24 * 60 * 60 * 1000;

        List<MoodLog> logs = moodLogRepository
                .findByUserClientIdAndCreatedAtBetween(clientId, weekAgo, now);

        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Mood log for the last week"));
        return Optional.of(content);
    }

    public Optional<Content> monthMoodLogCommand(long chatId, Long clientId) {
        long now = System.currentTimeMillis();
        long monthAgo = now - 30L * 24 * 60 * 60 * 1000;

        List<MoodLog> logs = moodLogRepository
                .findByUserClientIdAndCreatedAtBetween(clientId, monthAgo, now);

        var content = new Content(chatId);
        content.setText(formatMoodLogs(logs, "Mood log for the last month"));
        return Optional.of(content);
    }

    private String formatMoodLogs(List<MoodLog> logs, String title) {
        if (logs.isEmpty()) {
            return title + ":\nNo mood logs found.";
        }
        var sb = new StringBuilder(title + ":\n");
        logs.forEach(log -> {
            String formattedDate = formatter.format(Instant.ofEpochMilli(log.getCreatedAt()));
            sb.append(formattedDate).append(": ").append(log.getMood().getText()).append("\n");
        });
        return sb.toString();
    }

    public Optional<Content> awards(long chatId, Long clientId) {
        Content content = new Content(chatId);
        List<Achievement>  achievements = achievementRepository.findByUserClientId(clientId);

        if (achievements.isEmpty()) {
            content.setText("У вас пока нет достижений 😊");
            return Optional.of(content);
        }

        StringBuilder sb = new StringBuilder("Ваши достижения:\n\n");

        for (Achievement achievement : achievements) {
            Award award = achievement.getAward();

            sb.append("— ")
                    .append(award.getTitle())
                    .append("\n")
                    .append(award.getDescription())
                    .append("\n\n");
        }

        content.setText(sb.toString());
        return Optional.of(content);
    }
}
