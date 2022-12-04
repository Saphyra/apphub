package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.request.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.user.model.response.BanResponse;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class MarkUserForDeletionServiceTest {
    private static final LocalDate DATE = LocalDate.now();
    private static final Integer HOURS = 23;
    private static final Integer MINUTES = 25;
    private static final String TIME = HOURS + ":" + MINUTES;
    private static final UUID DELETED_USER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD_TOKEN = "password-token";
    private static final String PASSWORD = "password";

    @Mock
    private BanResponseQueryService banResponseQueryService;

    @Mock
    private UserDao userDao;

    @Mock
    private CheckPasswordService checkPasswordService;

    @InjectMocks
    private MarkUserForDeletionService underTest;

    @Mock
    private User user;

    @Mock
    private User deletedUser;

    @Mock
    private BanResponse banResponse;

    @Before
    public void setUp() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
    }

    @Test
    public void nullPassword() {
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(TIME)
            .password(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void nullDate() {
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .date(null)
            .time(TIME)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "date", "must not be null");
    }

    @Test
    public void nullTime() {
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(null)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "time", "must not be null");
    }

    @Test
    public void incorrectTime() {
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time("adf")
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "time", "invalid value");
    }

    @Test
    public void markUserForDeletion() {
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(TIME)
            .password(PASSWORD)
            .build();

        given(banResponseQueryService.getBans(DELETED_USER_ID)).willReturn(banResponse);
        given(userDao.findByIdValidated(DELETED_USER_ID)).willReturn(deletedUser);

        BanResponse result = underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID);

        verify(deletedUser).setMarkedForDeletion(true);
        verify(deletedUser).setMarkedForDeletionAt(LocalDateTime.of(DATE, LocalTime.of(HOURS, MINUTES, 0)));
        verify(userDao).save(deletedUser);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);

        assertThat(result).isEqualTo(banResponse);
    }
}