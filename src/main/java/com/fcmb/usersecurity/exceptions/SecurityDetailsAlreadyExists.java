package com.fcmb.usersecurity.exceptions;

public class SecurityDetailsAlreadyExists extends RuntimeException {
    public SecurityDetailsAlreadyExists(String message) {
        super(message);
    }
}
