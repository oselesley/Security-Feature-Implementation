package com.fcmb.usersecurity.services;
import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.UserSecurityDetail;

import java.math.BigDecimal;

/**
 * UserSecurityDetailTransactionService: is an interface with a method perform transaction
 * that will be called on every transaction
 */
public interface UserSecurityDetailTransactionService {
    boolean verifyTransactionLimitNotExceededAndTwoFactorEnforced(String deviceId, BigDecimal transactionAmount);

    void updateUserSecurityDetails(UserSecurityDetail userSecurityDetail, BigDecimal amount);
}
