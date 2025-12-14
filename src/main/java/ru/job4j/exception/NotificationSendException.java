package ru.job4j.exception;

public class NotificationSendException extends ServiceException {

    public NotificationSendException(String message, Throwable cause) {
        super(message, cause);
    }
}
