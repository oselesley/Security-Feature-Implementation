package com.fcmb.usersecurity.repositories;

import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSecurityDetailRepository extends JpaRepository<UserSecurityDetail, Long> {
    List<UserSecurityDetail> findUserSecurityDetailByUser (User user);

    Optional<UserSecurityDetail> findUserSecurityDetailByDeviceId (String deviceId);
}
