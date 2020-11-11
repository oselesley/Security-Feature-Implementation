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
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
public class UserSecurityDetailServiceImpl implements UserSecurityDetailService, UserSecurityDetailTransactionService {
    private final UserSecurityDetailRepository userSecurityDetailRepository;
    private final UserSecurityDetailUtils userSecurityDetailUtils;
    Logger logger = LoggerFactory.logger(UserSecurityDetailService.class);

    public UserSecurityDetailServiceImpl(UserSecurityDetailRepository userSecurityDetailRepository,
                                         UserSecurityDetailUtils userSecurityDetailUtils) {
        this.userSecurityDetailRepository = userSecurityDetailRepository;
        this.userSecurityDetailUtils = userSecurityDetailUtils;
    }

    /**
     * When a new user is onboarded into the app, the UserSecurityDetails for that user is created
     * @param user
     */
    @Override
    public void addNewOnboardedUser (User user, String deviceId, BigDecimal transactionLimit, Integer transactionCount) {
        createUserSecurityDetails(user, deviceId, transactionLimit, transactionCount);
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

    @Override
    public UserSecurityDetail getUserSecurityDetail(String deviceId) {
        Optional<UserSecurityDetail> userSecurityDetail = userSecurityDetailRepository.findUserSecurityDetailByDeviceId(deviceId);
        if (!userSecurityDetail.isPresent())
            throw new SecurityDetailNotFoundException(String.format("security details for device with id %s not found!", deviceId));

        return userSecurityDetail.get();
    }

    @Override
    public void addNewDevice(User user, String newDeviceId, BigDecimal transactionLimit, Integer transactionCount) {
        createUserSecurityDetails(user, newDeviceId, transactionLimit, transactionCount);
    }

    /**
     * fetchUserStatus returns the current status of the users security details
     * @param user
     * @param deviceId
     * @return Status
     */
    @Override
    public Status fetchUserStatus(User user, String deviceId) {
        UserSecurityDetail userSecurityDetail = getUserSecurityDetail(deviceId);
        return userSecurityDetailUtils.getUserStatusFromUserSecurityDetails(userSecurityDetail);
    }

    @Override
    public boolean verifyTransactionLimitNotExceededAndTwoFactorEnforced(String deviceId, BigDecimal transactionAmount, Integer count) {
        return verifyTransactionLimitNotExceeded(deviceId, transactionAmount) && verifyNumberOfTransactionsLessThanTransactionCount(deviceId, transactionAmount, count);
    }

    @Override
    public void updateUserSecurityDetails(UserSecurityDetail userSecurityDetail, BigDecimal amount, Integer transactionCount) {
        List<UserSecurityDetail> userSecurityDetails = getUserSecurityDetail(userSecurityDetail.getUser());
        if (userSecurityDetail.getTwoFactorEnforced()) userSecurityDetail.incrementTransactionCount();

        if (userSecurityDetail.getTransactionCount() >= transactionCount) userSecurityDetail.setTwoFactorEnforced(false);

        if (userSecurityDetail.getTimeOfFirstTransaction() == null)
            userSecurityDetail.setTimeOfFirstTransaction(LocalDateTime.now());

        if (userSecurityDetail.getTimeOfFirstTransaction().plusHours(24).isAfter(LocalDateTime.now())) userSecurityDetail.setLimitFlag(false);

        if (userSecurityDetail.getLimit().compareTo(amount) < 0 ||
                userSecurityDetail.getLimit().compareTo(userSecurityDetail.getTotalTransactionAmount().add(amount)) < 0)
            throw new TransactionLimitExceededException("transaction limit exceeded!");

        for (UserSecurityDetail usd : userSecurityDetails) {
               usd.setTimeOfFirstTransaction(userSecurityDetail.getTimeOfFirstTransaction());
               usd.setLimitFlag(userSecurityDetail.getLimitFlag());
               usd.setTotalTransactionAmount(userSecurityDetail.getTotalTransactionAmount());
               userSecurityDetailRepository.save(usd);
        }

        userSecurityDetailRepository.save(userSecurityDetail);
    }

    /**
     * VerifyTwoFactorEnforced: a helper method that verifies the twoFactorEnforced status before transaction
     * @param deviceId
     * @param transactionAmount
     * @return boolean
     */
    private boolean verifyNumberOfTransactionsLessThanTransactionCount(String deviceId, BigDecimal transactionAmount, Integer count) {
        UserSecurityDetail userSecurityDetail = getUserSecurityDetail(deviceId);
        // Checks if the userSecurity details still has a two factor enforced flag
        return userSecurityDetail.getTransactionCount() < count;
    }

    /**
     * VerifyTransactionLimitNotExceeded: verifies that the users transaction limit hasn't exceeded
     * @param deviceId
     * @param transactionAmount
     * @return boolean
     */
    private boolean verifyTransactionLimitNotExceeded(String deviceId, BigDecimal transactionAmount) {
        UserSecurityDetail userSecurityDetail = getUserSecurityDetail(deviceId);
        if (userSecurityDetail.getTimeOfFirstTransaction().plusHours(24).isAfter(LocalDateTime.now())) {
            return userSecurityDetail.getLimit().compareTo(transactionAmount) >= 0 &&
                    userSecurityDetail.getLimit().compareTo(userSecurityDetail.getTotalTransactionAmount().add(transactionAmount)) >= 0;
        }
        return true;
    }

    private void createUserSecurityDetails (User user, String deviceId, BigDecimal transactionLimit, Integer transactionCount) {
        UserSecurityDetail newUserSecurityDetail;
        UserSecurityDetail userSecurityDetail = null;
        try {
           userSecurityDetail = getUserSecurityDetail(deviceId);
        } catch (SecurityDetailNotFoundException e) {
            logger.info(e.getMessage());
        }


        if (userSecurityDetail != null) throw new SecurityDetailsAlreadyExists(String.format("security details for device with id %s already exists!", deviceId));
        List<UserSecurityDetail> securityDetails = userSecurityDetailRepository.findUserSecurityDetailByUser(user);

        // If user previously exists, create a new security detail with the users previous totalTransactionAmount
        // Else create a new security detail with a fresh transaction count, i.e starting from 3
        if (securityDetails.size() > 0) {
            newUserSecurityDetail = userSecurityDetailUtils
                    .createNewUserSecurityDetails(user, deviceId, transactionLimit, securityDetails.get(0).getTransactionCount());

        } else newUserSecurityDetail = userSecurityDetailUtils
                .createNewUserSecurityDetails(user, deviceId, transactionLimit, transactionCount);

        userSecurityDetailRepository.save(newUserSecurityDetail);
    }
}
