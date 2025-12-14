package ru.job4j.exception;

public class ReminderSendException extends ServiceException {

    public ReminderSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
