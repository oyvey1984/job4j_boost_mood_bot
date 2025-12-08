package ru.job4j.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.model.MoodLog;
import ru.job4j.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoodLogFakeRepository
        extends CrudRepositoryFake<MoodLog, Long>
        implements MoodLogRepository {

    public List<MoodLog> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public List<MoodLog> findByUserId(Long userId) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    public Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId() == userId)
                .sorted(Comparator.comparing(MoodLog::getCreatedAt).reversed());
    }

    @Override
    @Query("SELECT ml FROM MoodLog ml WHERE ml.createdAt BETWEEN :startOfDay AND :endOfDay")
    public List<MoodLog> findTodayVotes(@Param("startOfDay") long startOfDay,
                                        @Param("endOfDay") long endOfDay) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getCreatedAt() >= startOfDay)
                .filter(moodLog -> moodLog.getCreatedAt() <= endOfDay)
                .collect(Collectors.toList());
    }

    @Override
    public List<MoodLog> findByUserClientIdAndCreatedAtBetween(Long clientId, long from, long to) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getClientId() == clientId)
                .filter(moodLog -> moodLog.getCreatedAt() >= from && moodLog.getCreatedAt() <= to)
                .collect(Collectors.toList());
    }


    @Override
    @Query("SELECT ml FROM MoodLog ml WHERE ml.user.id = :userId AND ml.createdAt >= :weekStart")
    public List<MoodLog> findMoodLogsForWeek(@Param("userId") Long userId,
                                             @Param("weekStart") long weekStart) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId() == userId)
                .filter(moodLog -> moodLog.getCreatedAt() >= weekStart)
                .collect(Collectors.toList());
    }

    @Override
    @Query("SELECT ml FROM MoodLog ml WHERE ml.user.id = :userId AND ml.createdAt >= :monthStart")
    public List<MoodLog> findMoodLogsForMonth(@Param("userId") Long userId,
                                              @Param("monthStart") long monthStart) {
        return memory.values().stream()
                .filter(moodLog -> moodLog.getUser().getId() == userId)
                .filter(moodLog -> moodLog.getCreatedAt() >= monthStart)
                .collect(Collectors.toList());
    }
}
