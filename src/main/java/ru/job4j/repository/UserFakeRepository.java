package ru.job4j.repository;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.model.MoodLog;
import ru.job4j.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class UserFakeRepository
        extends CrudRepositoryFake<User, Long>
        implements UserRepository {

    private final MoodLogRepository moodLogRepository;

    public UserFakeRepository(MoodLogRepository moodLogRepository) {
        this.moodLogRepository = moodLogRepository;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(memory.values());
    }

    @Override
    public Optional<User> findByChatId(Long chatId) {
        return memory.values().stream()
                .filter(user -> user.getChatId() == chatId)
                .findFirst();
    }

    @Override
    public Optional<User> findByClientId(Long clientId) {
        return memory.values().stream()
                .filter(user -> user.getId() == clientId)
                .findFirst();
    }

    @Override
    public List<User> findUsersWithoutVotesToday(long startOfDay, long endOfDay) {
        List<MoodLog> todayVotes = moodLogRepository.findTodayVotes(startOfDay, endOfDay);
        
        Set<Long> userIdsWithVotesToday = todayVotes.stream()
                .map(moodLog -> moodLog.getUser().getId())
                .collect(Collectors.toSet());

        return memory.values().stream()
                .filter(user -> !userIdsWithVotesToday.contains(user.getId()))
                .collect(Collectors.toList());
    }
}
