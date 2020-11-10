package com.fcmb.usersecurity.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserSecurityDetail {
    User userId;
    String deviceId;
    BigDecimal totalTransactionAmount;
    Boolean limitFlag;
    Integer transactionCount;
    Boolean twoFactorEnforced;
    LocalDateTime timeOfFirstTransaction;
    LocalDateTime timeRegistered;
}
