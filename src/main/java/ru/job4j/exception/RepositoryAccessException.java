package ru.job4j.exception;

public class RepositoryAccessException extends ServiceException {

    public RepositoryAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
