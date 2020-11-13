package com.fcmb.usersecurity.utils;
/**
 * Utility class for the UserSecurityDetailsMethod
 */

import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.Status;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class UserSecurityDetailUtils {
    /**
     * Maps a userSecurityDetails Object to a Status Object
     * @param userSecurityDetail
     * @return Status
     */
    public Status getUserStatusFromUserSecurityDetails (UserSecurityDetail userSecurityDetail) {
        return new Status()
                .withLimit(userSecurityDetail.getLimit())
                .withLimitEnforced(userSecurityDetail.getLimitFlag())
                .withTotalTransactionAmount(userSecurityDetail.getTotalTransactionAmount())
                .withtwoFactorEnforced(userSecurityDetail.getTwoFactorEnforced())
                .withUser(userSecurityDetail.getUser());
    }

    /**
     * Creates and returns a new UserSecurityDetailsObject
     * @return UserSecurityDetail
     */
    public UserSecurityDetail createNewUserSecurityDetails (User user, String deviceId, BigDecimal transactionLimit, Integer transactionCount) {
        return new UserSecurityDetail()
                .withUser(user)
                .withDeviceId(deviceId)
                .withLimit(transactionLimit)
                .withTransactionCount(transactionCount)
                .withTotalTransactionAmount(BigDecimal.valueOf(0));
    }
}
