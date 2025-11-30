package ru.job4j.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.job4j.model.MoodLog;
import ru.job4j.model.User;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();
    List<MoodLog> findByUserClientIdAndCreatedAtBetween(Long clientId, long from, long to);
    List<User> findUsersWhoDidNotVoteToday(long startOfDay, long endOfDay);
    List<MoodLog> findByUserId(Long userId);
    Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<MoodLog> findMoodLogsForWeek(Long userId, long weekStart);
    List<MoodLog> findMoodLogsForMonth(Long userId, long monthStart);
}
