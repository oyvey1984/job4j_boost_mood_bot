package ru.job4j.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.job4j.model.MoodLog;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public interface MoodLogRepository extends CrudRepository<MoodLog, Long> {
    List<MoodLog> findAll();

    List<MoodLog> findByUserClientIdAndCreatedAtBetween(Long clientId, long from, long to);

    @Query("SELECT ml FROM MoodLog ml WHERE ml.createdAt BETWEEN :startOfDay AND :endOfDay")
    List<MoodLog> findTodayVotes(@Param("startOfDay") long startOfDay,
                                 @Param("endOfDay") long endOfDay);

    @Query("""
            SELECT ml
            FROM MoodLog ml
            WHERE ml.createdAt BETWEEN :startOfDay AND :endOfDay
            AND ml.createdAt = (
                SELECT MAX(ml2.createdAt)
                FROM MoodLog ml2
                WHERE ml2.user.id = ml.user.id
                AND ml2.createdAt BETWEEN :startOfDay AND :endOfDay
            )
            """)
    List<MoodLog> findTodayLastVotes(@Param("startOfDay") long startOfDay,
                                     @Param("endOfDay") long endOfDay);

    @Query("""
            SELECT ml
            FROM MoodLog ml
            JOIN ml.user u
            WHERE u.adviceEnabled = true
            AND ml.createdAt BETWEEN :startOfDay AND :endOfDay
            AND ml.createdAt = (
                SELECT MAX(ml2.createdAt)
                FROM MoodLog ml2
                WHERE ml2.user.id = u.id
                AND ml2.createdAt BETWEEN :startOfDay AND :endOfDay
            )
            """)
    List<MoodLog> findTodayLastVotesWithAdviceEnabled(@Param("startOfDay") long startOfDay,
                                                      @Param("endOfDay") long endOfDay);


    List<MoodLog> findByUserId(Long userId);

    @Query("""
            SELECT ml
            FROM MoodLog ml
            WHERE ml.user.id = :userId
            AND ml.createdAt = (
                SELECT MAX(ml2.createdAt)
                FROM MoodLog ml2
                WHERE ml2.user.id = :userId
                AND ml2.createdAt BETWEEN :startOfDay AND :endOfDay
            )
            """)
    Optional<MoodLog> findTodayLastVoteByUserId(
            @Param("userId") Long userId,
            @Param("startOfDay") long startOfDay,
            @Param("endOfDay") long endOfDay
    );

    Stream<MoodLog> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("SELECT ml FROM MoodLog ml WHERE ml.user.id = :userId "
            + "AND ml.createdAt >= :weekStart")
    List<MoodLog> findMoodLogsForWeek(@Param("userId") Long userId,
                                      @Param("weekStart") long weekStart);

    @Query("SELECT ml FROM MoodLog ml WHERE ml.user.id = :userId "
            + "AND ml.createdAt >= :monthStart")
    List<MoodLog> findMoodLogsForMonth(@Param("userId") Long userId,
                                       @Param("monthStart") long monthStart);
}
