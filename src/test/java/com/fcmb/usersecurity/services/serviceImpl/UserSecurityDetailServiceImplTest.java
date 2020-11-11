package com.fcmb.usersecurity.services.serviceImpl;

import com.fcmb.usersecurity.exceptions.SecurityDetailNotFoundException;
import com.fcmb.usersecurity.exceptions.SecurityDetailsAlreadyExists;
import com.fcmb.usersecurity.models.Status;
import com.fcmb.usersecurity.models.User;
import com.fcmb.usersecurity.models.UserSecurityDetail;
import com.fcmb.usersecurity.repositories.UserSecurityDetailRepository;
import com.fcmb.usersecurity.services.UserSecurityDetailService;
import com.fcmb.usersecurity.utils.UserSecurityDetailUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.logging.Logger;
import org.junit.platform.commons.logging.LoggerFactory;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserSecurityDetailServiceImplTest {
    UserSecurityDetailServiceImpl userSecurityDetailService;
    UserSecurityDetailRepository userSecurityDetailRepository;
    UserSecurityDetailUtils userSecurityDetailUtils;
    UserSecurityDetail userSecurityDetail;
    User user;
    String DEVICE_ID = "2XF4ERY120009W";
    Logger logger = LoggerFactory.getLogger(UserSecurityDetailServiceImplTest.class);

    @BeforeEach
    void setUp() {
        userSecurityDetailRepository = Mockito.mock(UserSecurityDetailRepository.class);
        userSecurityDetailUtils = new UserSecurityDetailUtils();
        userSecurityDetailService = new UserSecurityDetailServiceImpl(userSecurityDetailRepository, userSecurityDetailUtils);
        user = new User();
        user.setId(BigDecimal.ONE);
        userSecurityDetail = new UserSecurityDetail()
                .withUser(user)
                .withDeviceId(DEVICE_ID)
                .withLimit(BigDecimal.valueOf(50_000))
                .withTransactionCount(3);
    }

    @Test
    @DisplayName("Should not add new onboarded user 🥙")
    void testDoesNotAddNewOnboardedUser() {
        assertDoesNotThrow(() -> {
            userSecurityDetailService.addNewOnboardedUser(user, DEVICE_ID, BigDecimal.valueOf(50_000), 3);
        });
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.of(userSecurityDetail));

        assertThrows(SecurityDetailsAlreadyExists.class, () -> {
            userSecurityDetailService.addNewOnboardedUser(user, DEVICE_ID, BigDecimal.valueOf(50_000), 3);
        });
    }

    @Test
    @DisplayName("Should add new onboarded user 🍔")
    void testAddNewOnboardedUser() {
        assertDoesNotThrow(() -> {
            userSecurityDetailService.addNewOnboardedUser(user, DEVICE_ID, BigDecimal.valueOf(50_000), 3);
        });
        userSecurityDetail.setDeviceId("OTYREIM90WPOE");
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.empty());

        assertDoesNotThrow(() -> {
            userSecurityDetailService.addNewOnboardedUser(user, DEVICE_ID, BigDecimal.valueOf(50_000), 3);
        });
    }

    @Test
    @DisplayName("Should not get userSecurityDetail by deviceId 🥮")
    void testDoesNotGetUserSecurityDetailByDeviceId() {
        assertThrows(SecurityDetailNotFoundException.class, () -> {
            userSecurityDetailService.getUserSecurityDetail(DEVICE_ID);
        });
    }

    @Test
    @DisplayName("Should get userSecurityDetail by deviceId 🎂")
    void testGetUserSecurityDetailByDeviceId() {
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.of(userSecurityDetail));
        assertDoesNotThrow(() -> {
            UserSecurityDetail usd = userSecurityDetailService.getUserSecurityDetail(DEVICE_ID);
            assertAll(() -> {
                assertNotNull(usd);
                assertEquals(true, usd.getLimitFlag());
                assertEquals(3, usd.getTransactionCount());
                assertEquals(usd.getDeviceId(), DEVICE_ID);
            });
        });
    }

    @Test
    @DisplayName("Should get userSecurityDetail by user 🍫")
    void testGetUserSecurityDetailByUser() {
        List<UserSecurityDetail> userSecurityDetails = userSecurityDetailService.getUserSecurityDetail(user);
        assertNotNull(userSecurityDetails);
        assertEquals(0, userSecurityDetails.size());
    }

    @Test
    @DisplayName("Should not add new device 🍫")
    void testShouldNotAddNewDevice() {
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.of(userSecurityDetail));

        assertThrows(SecurityDetailsAlreadyExists.class, () -> {
            userSecurityDetailService.addNewDevice(user, DEVICE_ID, BigDecimal.valueOf(50_000), 3);
        });
    }

    @Test
    @DisplayName("Should add new device 🐹")
    void testShouldAddNewDevice() {
        assertDoesNotThrow(() -> {
            userSecurityDetailService.addNewDevice(user, DEVICE_ID, BigDecimal.valueOf(50_000), 3);
        });
    }

    @Test
    @DisplayName("Should fetch user status 🐸")
    void testFetchUserStatus() {
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.of(userSecurityDetail));
        Status status = userSecurityDetailService.fetchUserStatus(user, DEVICE_ID);
        assertAll(() -> {
            assertNotNull(status);
            assertEquals(status.getLimit(), userSecurityDetail.getLimit());
            assertEquals(status.getLimitEnforced(), userSecurityDetail.getLimitFlag());
            assertEquals(status.getTotalTransactionAmount(), userSecurityDetail.getTotalTransactionAmount());
            assertEquals(status.getTwoFactorEnforced(), userSecurityDetail.getTwoFactorEnforced());
        });
    }

    @Test
    @DisplayName("Should verify transaction limit not exceeded and two factor enforced 🦁")
    void testVerifyTransactionLimitNotExceededAndTwoFactorEnforced() {
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.of(userSecurityDetail));
        Boolean result = userSecurityDetailService.verifyTransactionLimitNotExceededAndTwoFactorEnforced(DEVICE_ID, BigDecimal.valueOf(50_000));
        assertFalse(result);
        userSecurityDetail.setLimitFlag(false);
        userSecurityDetail.setTwoFactorEnforced(false);
        when(userSecurityDetailRepository.findUserSecurityDetailByDeviceId(DEVICE_ID)).thenReturn(Optional.of(userSecurityDetail));

        result = userSecurityDetailService.verifyTransactionLimitNotExceededAndTwoFactorEnforced(DEVICE_ID, BigDecimal.valueOf(50_000));
        assertTrue(result);
    }
}