package ru.job4j.services;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.job4j.content.Content;
import ru.job4j.content.SentContent;
import ru.job4j.exception.ReminderSendException;
import ru.job4j.model.User;
import ru.job4j.repository.UserRepository;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class ReminderService {
    private final SentContent sentContent;
    private final UserRepository userRepository;
    private final TgUI tgUI;

    public ReminderService(SentContent sentContent,
                           UserRepository userRepository,
                           TgUI tgUI) {
        this.sentContent = sentContent;
        this.userRepository = userRepository;
        this.tgUI = tgUI;
    }

    @Scheduled(cron = "0 30 15 * * *")
    public void remindUsers() {
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
            List<User> usersWithoutVotes = userRepository.findUsersForReminder(startOfDay, endOfDay);
            for (var user : usersWithoutVotes) {
                var content = new Content(user.getChatId());
                content.setText("Как настроение?");
                content.setMarkup(tgUI.buildButtons());
                sentContent.sent(content);
            }
        } catch (Exception e) {
            throw new ReminderSendException("Failed to send daily reminders", e);
        }
    }
}
