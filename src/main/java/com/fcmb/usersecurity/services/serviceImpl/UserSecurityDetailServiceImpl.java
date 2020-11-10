package com.fcmb.usersecurity.services.serviceImpl;

import com.fcmb.usersecurity.exceptions.SecurityDetailNotFoundException;
import com.fcmb.usersecurity.exceptions.SecurityDetailsAlreadyExists;
import com.fcmb.usersecurity.exceptions.TransactionLimitExceededException;
import com.fcmb.usersecurity.models.Status;
import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import com.fcmb.usersecurity.repositories.UserSecurityDetailRepository;
import com.fcmb.usersecurity.services.UserSecurityDetailService;
import com.fcmb.usersecurity.services.UserSecurityDetailTransactionService;
import com.fcmb.usersecurity.utils.UserSecurityDetailUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UserSecurityDetailServiceImpl implements UserSecurityDetailService, UserSecurityDetailTransactionService {
    private final BigDecimal TRANSACTION_LIMIT = BigDecimal.valueOf(50_000);
    private final Integer TRANSACTION_COUNT = 3;


    @Autowired
    private UserSecurityDetailRepository userSecurityDetailRepository;

    @Autowired
    private UserSecurityDetailUtils userSecurityDetailUtils;

    /**
     * When a new user is onboarded into the app, the UserSecurityDetails for that user is created
     * @param user
     */
    @Override
    public void addNewOnboardedUser(User user, String deviceId) {
        UserSecurityDetail newUserSecurityDetail = null;
        UserSecurityDetail userSecurityDetail = getUserSecurityDetail(deviceId);

        if (userSecurityDetail != null) throw new SecurityDetailsAlreadyExists(String.format("security details for device with id %s already exists!", deviceId));
        List<UserSecurityDetail> securityDetails = userSecurityDetailRepository.findUserSecurityDetailByUser(user);

        // If user previously exists, create a new security detail with the users former transaction count
        // Else create a new security detail with a fresh transaction count, i.e starting from 3
        if (securityDetails.size() > 0) {
            newUserSecurityDetail = userSecurityDetailUtils
                    .createNewUserSecurityDetails(user, deviceId, TRANSACTION_LIMIT, securityDetails.get(0).getTransactionCount());

        } else newUserSecurityDetail = userSecurityDetailUtils
                .createNewUserSecurityDetails(user, deviceId, TRANSACTION_LIMIT, TRANSACTION_COUNT);

        userSecurityDetailRepository.save(newUserSecurityDetail);
    }


    /**
     * Fetches a list of userSecurityDetails
     * @param user
     * @return List<UserSecurityDetails>
     */
    @Override
    public List<UserSecurityDetail> getUserSecurityDetail(User user) {
      return userSecurityDetailRepository
                .findUserSecurityDetailByUser(user);
    }

    public UserSecurityDetail getUserSecurityDetail(String deviceId) {
        Optional<UserSecurityDetail> userSecurityDetail = userSecurityDetailRepository.findUserSecurityDetailByDeviceId(deviceId);
        if (!userSecurityDetail.isPresent())
            throw new SecurityDetailNotFoundException(String.format("security details for device with id %s not found!", deviceId));

        return userSecurityDetail.get();
    }

    @Override
    public void addNewDevice() {
        // Similar logic with onBoarding User;
    }

    @Override
    public Status fetchUserStatus(User user, String deviceId) {
        UserSecurityDetail userSecurityDetail = getUserSecurityDetail(deviceId);
        return userSecurityDetailUtils.getUserStatusFromUserSecurityDetails(userSecurityDetail);
    }


    /**
     * This method is called on every user transaction
     * @param user
     * @return
     */
    @Override
    public void performTransaction(User user, BigDecimal transactionAmount) {
        List<UserSecurityDetail> userSecurityDetails = getUserSecurityDetail(user);
        for (UserSecurityDetail userSecurityDetail : userSecurityDetails) {
            LocalDateTime timeOfFirstTransaction = userSecurityDetail.getTimeOfFirstTransaction();
            BigDecimal limit = userSecurityDetail.getLimit();
            // If the time of first transaction is null set the time
            if (userSecurityDetail.getTimeOfFirstTransaction() == null)
                userSecurityDetail.setTimeOfFirstTransaction(LocalDateTime.now());

            // Checks if the userSecurity details still has a two factor enforced flag
            // If it does it decrements the transaction count and checks if the transaction count
            // equals zero, then sets two factor enforced to false;
            if (userSecurityDetail.getTwoFactorEnforced()) {
                userSecurityDetail.decrementTransactionCount();
                if (userSecurityDetail.getTransactionCount() == 0) {
                    userSecurityDetail.setTwoFactorEnforced(false);
                }
            }

            // If its 24hours since the time of first transaction, set limit flag to false
            // else check if the transaction amount is greater than the specified limit
            // or if the total transaction amount + the transaction amount is greater than the
            // specified limit
            if (timeOfFirstTransaction.plusHours(24).isAfter(LocalDateTime.now())) {
                userSecurityDetail.setLimitFlag(false);
            }else if (userSecurityDetail.getLimitFlag()) {
                if (limit.compareTo(transactionAmount) < 0 ||
                        userSecurityDetail.getTotalTransactionAmount().add(transactionAmount).compareTo(limit) < 0) {
                    throw new TransactionLimitExceededException("user transaction limit is exceeded!");
                } else {
                    userSecurityDetail.setTotalTransactionAmount(userSecurityDetail.getTotalTransactionAmount().add(transactionAmount));
                }

            }

            userSecurityDetailRepository.save(userSecurityDetail);
        }
    }
}
