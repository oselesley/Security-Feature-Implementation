package com.fcmb.usersecurity.services;

import com.fcmb.usersecurity.models.Status;
import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public interface UserSecurityDetailService {
    void addNewOnboardedUser (User user, String deviceId, BigDecimal transactionLimit, Integer transactionCount);

    void addNewDevice(User user, String deviceId, BigDecimal transactionLimit, Integer transactionCount);

    List<UserSecurityDetail> getUserSecurityDetail(User user);

    Status fetchUserStatus (User user, String deviceId);
}
