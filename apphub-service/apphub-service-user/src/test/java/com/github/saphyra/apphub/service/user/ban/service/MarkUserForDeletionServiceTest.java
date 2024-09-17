package com.github.saphyra.apphub.service.user.ban.service;

import com.github.saphyra.apphub.api.user.model.ban.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.api.user.model.ban.BanResponse;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class MarkUserForDeletionServiceTest {
    private static final UUID DELETED_USER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    private static final String MARKED_FOR_DELETION_AT = "2024-05-06T11:39";

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

    @Test
    public void nullPassword() {
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(MARKED_FOR_DELETION_AT)
            .password(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void nullMarkedForDeletionAt() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(null)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "markForDeletionAt", "must not be null");
    }

    @Test
    public void invalidMarkedForDeletionAt() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .markForDeletionAt("adf")
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID));

        ExceptionValidator.validateInvalidParam(ex, "markForDeletionAt", "failed to parse");
    }

    @Test
    public void markUserForDeletion() {
        given(checkPasswordService.checkPassword(USER_ID, PASSWORD)).willReturn(user);
        MarkUserForDeletionRequest request = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(MARKED_FOR_DELETION_AT)
            .password(PASSWORD)
            .build();

        given(banResponseQueryService.getBans(DELETED_USER_ID)).willReturn(banResponse);
        given(userDao.findByIdValidated(DELETED_USER_ID)).willReturn(deletedUser);

        BanResponse result = underTest.markUserForDeletion(DELETED_USER_ID, request, USER_ID);
        verify(deletedUser).setMarkedForDeletion(true);
        verify(deletedUser).setMarkedForDeletionAt(LocalDateTime.of(LocalDate.of(2024, 5, 6), LocalTime.of(11, 39, 0)));
        verify(userDao).save(deletedUser);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);

        assertThat(result).isEqualTo(banResponse);
    }
}