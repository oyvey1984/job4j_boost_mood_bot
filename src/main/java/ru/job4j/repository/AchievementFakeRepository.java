package ru.job4j.repository;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.model.Achievement;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AchievementFakeRepository
        extends CrudRepositoryFake<Achievement, Long>
        implements AchievementRepository {

    @Override
    public List<Achievement> findByUserClientId(Long clientId) {
        return memory.values().stream()
                .filter(a -> a.getUser().getClientId() == clientId)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByUserClientIdAndAwardId(long clientId, Long id) {
        return memory.values().stream()
                .anyMatch(a ->
                        a.getUser().getClientId() == clientId
                                && a.getAward().getId().equals(id)
                );
    }

    @Override
    public List<Achievement> findAll() {
        return new ArrayList<>(memory.values());
    }
}
