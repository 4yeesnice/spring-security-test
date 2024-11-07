package org.kg.secure.exceptions.token;

public class BadTokenException extends RuntimeException {
    public BadTokenException(String message) {
        super(message);
    }
}
