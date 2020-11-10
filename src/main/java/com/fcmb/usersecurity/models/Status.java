package com.fcmb.usersecurity.models;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class Status {
    private User user;
    Boolean twoFactorEnforced;
    Boolean limitEnforced;
    BigDecimal limit;
    BigDecimal totalTransactionAmount;

    public Status withUser(User user) {
        this.user = user;
        return this;
    }

    public Status withtwoFactorEnforced(boolean twoFactorEnforced) {
        this.twoFactorEnforced = twoFactorEnforced;
        return this;
    }

    public Status withLimitEnforced(boolean limitEnforced) {
        this.limitEnforced = limitEnforced;
        return this;
    }

    public Status withLimit(BigDecimal limit) {
        this.limit = limit;
        return this;
    }

    public Status withTotalTransactionAmount(BigDecimal totalTransactionAmount) {
        this.totalTransactionAmount = totalTransactionAmount;
        return this;
    }
}
