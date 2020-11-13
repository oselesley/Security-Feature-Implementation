package com.fcmb.usersecurity.services;
import com.fcmb.usersecurity.models.UserSecurityDetail;

import java.math.BigDecimal;

public interface UserSecurityDetailTransactionService {
    boolean verifyTransactionLimitNotExceededAndTwoFactorEnforced(String deviceId, BigDecimal transactionAmount,Integer count);

    void updateUserSecurityDetails(UserSecurityDetail userSecurityDetail, BigDecimal amount, Integer transactionCount);
}
