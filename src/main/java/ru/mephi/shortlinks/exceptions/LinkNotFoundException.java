package ru.mephi.shortlinks.exceptions;

public class LinkNotFoundException extends Exception {
    public LinkNotFoundException(String message) {
        super(message);
    }
}
