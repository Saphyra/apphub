package com.github.saphyra.apphub.service.user.data.dao;

import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.user.data.dao.role_deprecated.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.role_deprecated.RoleDto;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.dao.user_deprecated.DeprecatedUser;
import com.github.saphyra.apphub.service.user.data.dao.user_deprecated.DeprecatedUserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class UserMigrationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "email@test.com";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String LANGUAGE = "en";
    private static final LocalDateTime MARKED_FOR_DELETION_AT = LocalDateTime.now();
    private static final LocalDateTime LOCKED_UNTIL = LocalDateTime.now().plusDays(1);

    @Mock
    private DeprecatedUserDao deprecatedUserDao;

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private ErrorReporterService errorReporterService;

    @InjectMocks
    private UserMigrationService underTest;

    @Test
    void migrate_noUsers() {
        given(deprecatedUserDao.findAll()).willReturn(List.of());

        underTest.migrate();

        verifyNoInteractions(roleDao, userDao, errorReporterService);
    }

    @Test
    void migrate_success() {
        DeprecatedUser deprecatedUser = DeprecatedUser.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LANGUAGE)
            .markedForDeletionAt(MARKED_FOR_DELETION_AT)
            .passwordFailureCount(3)
            .lockedUntil(LOCKED_UNTIL)
            .build();

        RoleDto roleDto = RoleDto.builder()
            .roleId(UUID.randomUUID())
            .userId(USER_ID)
            .role(Role.ACCESS.name())
            .build();

        given(deprecatedUserDao.findAll()).willReturn(List.of(deprecatedUser));
        given(roleDao.getByUserId(USER_ID)).willReturn(List.of(roleDto));

        underTest.migrate();

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userDao).saveNew(userCaptor.capture());

        User savedUser = userCaptor.getValue();
        assertThat(savedUser.getUserId()).isEqualTo(USER_ID);
        assertThat(savedUser.getEmail()).isEqualTo(EMAIL);
        assertThat(savedUser.getUsername()).isEqualTo(USERNAME);
        assertThat(savedUser.getPassword()).isEqualTo(PASSWORD);
        assertThat(savedUser.getLanguage()).isEqualTo(LANGUAGE);
        assertThat(savedUser.getMarkedForDeletionAt()).isEqualTo(MARKED_FOR_DELETION_AT);
        assertThat(savedUser.getPasswordFailureCount()).isEqualTo(3);
        assertThat(savedUser.getLockedUntil()).isEqualTo(LOCKED_UNTIL);
        assertThat(savedUser.getRoles()).containsExactly(Role.ACCESS);

        verify(roleDao).delete(roleDto);
        verify(deprecatedUserDao).delete(deprecatedUser);
        verifyNoInteractions(errorReporterService);
    }

    @Test
    void migrate_errorHandled() {
        DeprecatedUser deprecatedUser = DeprecatedUser.builder()
            .userId(USER_ID)
            .email(EMAIL)
            .username(USERNAME)
            .password(PASSWORD)
            .language(LANGUAGE)
            .passwordFailureCount(0)
            .build();

        RuntimeException exception = new RuntimeException("test error");

        given(deprecatedUserDao.findAll()).willReturn(List.of(deprecatedUser));
        given(roleDao.getByUserId(USER_ID)).willThrow(exception);

        underTest.migrate();

        verify(errorReporterService).report("Failed migrating user " + USER_ID, exception);
        verifyNoInteractions(userDao);
    }
}