package ru.job4j.exception;

public class MoodNotFoundException extends RuntimeException {
    public MoodNotFoundException(Long moodId) {
        super("Mood not found " + moodId);
    }
}
