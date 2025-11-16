package ru.job4j.content;

public interface ContentProvider {
    Content byMood(Long chatId, Long moodId);
}
