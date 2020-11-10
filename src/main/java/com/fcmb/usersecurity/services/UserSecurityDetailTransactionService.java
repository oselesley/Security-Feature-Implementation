package com.fcmb.usersecurity.services;
import com.fcmb.usersecurity.models.User;

import java.math.BigDecimal;

/**
 * UserSecurityDetailTransactionService: is an interface with a method perform transaction
 * that will be called on every transaction
 */

public interface UserSecurityDetailTransactionService {
    void performTransaction(User user, BigDecimal transactionAmount);
}
