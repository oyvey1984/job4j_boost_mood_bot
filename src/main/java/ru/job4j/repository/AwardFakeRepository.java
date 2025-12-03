package ru.job4j.repository;

import org.springframework.test.fake.CrudRepositoryFake;
import ru.job4j.model.Award;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AwardFakeRepository
        extends CrudRepositoryFake<Award, Long>
        implements AwardRepository {

    private long seq = 1;

    @Override
    public Optional<Award> findByDaysRequired(int daysRequired) {
        return memory.values().stream()
                .filter(a -> a.getDays() == daysRequired)
                .findFirst();
    }

    @Override
    public List<Award> findAll() {
        return new ArrayList<>(memory.values()).stream()
                .sorted(Comparator.comparing(Award::getDays))
                .collect(Collectors.toList());
    }


    @Override
    public <S extends Award> S save(S entity) {
        if (entity.getId() == null) {
            entity.setId(seq++);
        }
        return super.save(entity);
    }

}
