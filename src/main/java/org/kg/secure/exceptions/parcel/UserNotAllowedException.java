package org.kg.secure.exceptions.parcel;

public class UserNotAllowedException extends RuntimeException {

    public UserNotAllowedException(String message) {
        super(message);
    }
}
