package com.fcmb.usersecurity.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import java.math.BigDecimal;

@Configuration
@PropertySources(@PropertySource("classpath:usersecurityconfig.properties"))
public class AppConfig {
    @Value("${user.transaction.limit}")
    BigDecimal transactionLimit;

    @Value("${user.transaction.count}")
    Integer transactionCount;

    @Bean
    public TransactionConstraints getTransactionConstraints () {
        return new TransactionConstraints()
                .withTransactionCount(transactionCount)
                .withTransactionLimit(transactionLimit);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
