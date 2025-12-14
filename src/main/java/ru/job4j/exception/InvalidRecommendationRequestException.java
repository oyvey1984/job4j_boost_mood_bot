package ru.job4j.exception;

public class InvalidRecommendationRequestException extends ServiceException {

    public InvalidRecommendationRequestException(String message) {
        super(message);
    }
}
