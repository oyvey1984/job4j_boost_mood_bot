package ru.job4j.services;

import org.springframework.stereotype.Service;
import ru.job4j.content.Content;
import ru.job4j.exception.ReminderSendException;
import ru.job4j.model.Mood;
import ru.job4j.model.MoodLog;
import ru.job4j.repository.MoodLogRepository;
import ru.job4j.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AdviceService {
    private final RecommendationEngine recommendationEngine;
    private final MoodLogRepository moodLogRepository;

    public AdviceService(RecommendationEngine recommendationEngine, MoodLogRepository moodLogRepository, UserRepository userRepository, TgUI tgUI) {
        this.recommendationEngine = recommendationEngine;
        this.moodLogRepository = moodLogRepository;
    }

    public List<Content> adviceUsers() {
        List<Content> contentList = new ArrayList<>();
        try {
            var startOfDay = LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            var endOfDay = LocalDate.now()
                    .plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() - 1;
            List<MoodLog> moodLogs = moodLogRepository.findTodayLastVotesWithAdviceEnabled(startOfDay, endOfDay);
            for (var moodLog : moodLogs) {
                Mood mood = moodLog.getMood();
                Content content = recommendationEngine.dailyRecommendation(
                        moodLog.getUser().getChatId(),
                        mood.getId(),
                        mood.isGood()
                );
                contentList.add(content);
            }
            return contentList;
        } catch (Exception e) {
            throw new ReminderSendException("Failed to send daily advice", e);
        }
    }

    public Optional<Content> personalAdvice(Long clientId) {
        try {
            var startOfDay = LocalDate.now()
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli();
            var endOfDay = LocalDate.now()
                    .plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli() - 1;
            Optional<MoodLog>  moodLogOptional = moodLogRepository
                    .findTodayLastVoteByUserId(clientId, startOfDay, endOfDay);
            MoodLog moodLog = moodLogOptional.orElseThrow(() -> new IllegalArgumentException("MoodLog не найден"));
            Mood mood = moodLog.getMood();
            Content content = recommendationEngine.dailyRecommendation(
                    moodLog.getUser().getChatId(),
                    mood.getId(),
                    mood.isGood()
                );
            return Optional.of(content);
            } catch (Exception e) {
            throw new ReminderSendException("Failed to send daily advice", e);
        }
    }
}
