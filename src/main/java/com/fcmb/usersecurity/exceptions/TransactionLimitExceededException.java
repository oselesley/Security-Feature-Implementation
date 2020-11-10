package com.fcmb.usersecurity.exceptions;

public class TransactionLimitExceededException extends RuntimeException {
    public TransactionLimitExceededException() {
        super("transaction limit is exceeded!");
    }

    public TransactionLimitExceededException(String message) {
        super(message);
    }
}
