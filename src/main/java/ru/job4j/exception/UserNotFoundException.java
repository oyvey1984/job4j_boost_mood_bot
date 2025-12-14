package ru.job4j.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long clientId) {
        super("User not found :" + clientId);
    }
}
