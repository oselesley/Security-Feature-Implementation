package com.fcmb.usersecurity.services;

import com.fcmb.usersecurity.models.Status;
import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserSecurityDetailService {
    void addNewOnboardedUser (User user, String deviceId);

    void addNewDevice();

    List<UserSecurityDetail> getUserSecurityDetail(User user);

    Status fetchUserStatus (User user, String deviceId);
}
