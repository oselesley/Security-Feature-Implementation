package com.fcmb.usersecurity.config;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter @Setter
public class TransactionConstraints {
    private BigDecimal transactionLimit;
    private Integer transactionCount;

    public TransactionConstraints withTransactionLimit(BigDecimal transactionLimit) {
        this.transactionLimit = transactionLimit;
        return this;
    }
    public TransactionConstraints withTransactionCount(Integer transactionCount) {
        this.transactionCount = transactionCount;
        return this;
    }
}
