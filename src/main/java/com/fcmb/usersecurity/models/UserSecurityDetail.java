package com.fcmb.usersecurity.models;

/**
 * UserSecurityDetailClass: Includes fields for tracking the user security information
 * like the total transaction amount a newly registered user has within 24hours of his
 * first transaction.
 *
 * Includes a builder implementation for easy object instantiation;
 *
 */

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Getter
@Setter
public class UserSecurityDetail {
    private BigInteger id;
    User user;
    String deviceId;
    BigDecimal limit;
    Boolean limitFlag = true;
    BigDecimal totalTransactionAmount;
    Integer transactionCount = 3;
    Boolean twoFactorEnforced = true;
    LocalDateTime timeOfFirstTransaction;
    @CreationTimestamp
    LocalDateTime timeRegistered;

    public UserSecurityDetail withUser(User user) {
        this.user = user;
        return this;
    }

    public UserSecurityDetail withDeviceId(String deviceId) {
        this.deviceId = deviceId;
        return this;
    }

    public UserSecurityDetail withLimit(BigDecimal limit) {
        this.limit = limit;
        return this;
    }
    public UserSecurityDetail withTotalTransactionAmount(BigDecimal totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
        return this;
    }

    public UserSecurityDetail withTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
        return this;
    }

    public void decrementTransactionCount() {
        transactionCount--;
    }
}
