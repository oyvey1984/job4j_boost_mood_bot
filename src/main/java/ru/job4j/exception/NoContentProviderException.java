package ru.job4j.exception;

public class NoContentProviderException extends RuntimeException {

    public NoContentProviderException() {
        super("No ContentProvider beans found. Check application configuration.");
    }
}
