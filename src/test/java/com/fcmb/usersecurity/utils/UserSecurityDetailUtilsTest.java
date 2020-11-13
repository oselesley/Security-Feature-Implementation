package com.fcmb.usersecurity.utils;

import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.Status;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class UserSecurityDetailUtilsTest {
    UserSecurityDetailUtils userSecurityDetailUtils;
    UserSecurityDetail userSecurityDetail;
    User user;
    String DEVICE_ID = "102EXD4500";
    BigDecimal TRANSACTION_LIMIT = BigDecimal.valueOf(50_000);

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(BigInteger.ONE);
        userSecurityDetail = new UserSecurityDetail()
                .withUser(user)
                .withDeviceId(DEVICE_ID)
                .withLimit(BigDecimal.valueOf(50_000))
                .withTransactionCount(3);

        userSecurityDetailUtils = new UserSecurityDetailUtils();
    }

    @Test
    void getUserStatusFromUserSecurityDetails() {
        Status status = userSecurityDetailUtils.getUserStatusFromUserSecurityDetails(userSecurityDetail);

        assertAll(() -> {
            assertNotNull(status);
            assertEquals(status.getLimit(), userSecurityDetail.getLimit());
            assertEquals(status.getLimitEnforced(), userSecurityDetail.getLimitFlag());
            assertEquals(status.getTotalTransactionAmount(), userSecurityDetail.getTotalTransactionAmount());
            assertEquals(status.getTwoFactorEnforced(), userSecurityDetail.getTwoFactorEnforced());
        });
    }

    @Test
    void createNewUserSecurityDetails() {
        UserSecurityDetail userSecurityDetail = userSecurityDetailUtils.createNewUserSecurityDetails(user, DEVICE_ID, TRANSACTION_LIMIT, 3);

        assertAll(() -> {
            assertNotNull(userSecurityDetail);
            assertEquals(userSecurityDetail.getDeviceId(), DEVICE_ID);
            assertEquals(userSecurityDetail.getTotalTransactionAmount(), BigDecimal.ZERO);
            assertEquals(userSecurityDetail.getTransactionCount(), 3);
            assertTrue(userSecurityDetail.getLimitFlag());
            assertTrue(userSecurityDetail.getTwoFactorEnforced());
        });
    }
}