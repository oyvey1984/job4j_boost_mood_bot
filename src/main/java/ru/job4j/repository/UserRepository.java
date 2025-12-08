package ru.job4j.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.job4j.model.User;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
    Optional<User> findByChatId(Long chatId);
    Optional<User> findByClientId(Long clientId);
    @Query("SELECT u FROM User u WHERE u.id NOT IN ("
            + "  SELECT ml.user.id FROM MoodLog ml "
            + "  WHERE ml.createdAt BETWEEN :startOfDay AND :endOfDay"
            + ")")
    List<User> findUsersWithoutVotesToday(@Param("startOfDay") long startOfDay,
                                          @Param("endOfDay") long endOfDay);
}
