package ru.job4j.exception;

public class RecommendationFailedException extends ServiceException {

    public RecommendationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
