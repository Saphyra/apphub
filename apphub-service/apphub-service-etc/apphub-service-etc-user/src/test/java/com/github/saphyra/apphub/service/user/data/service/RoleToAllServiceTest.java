package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBean;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.config.properties.AddRoleToAllProperties;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;

@ExtendWith(MockitoExtension.class)
public class RoleToAllServiceTest {
    private static final String RESTRICTED_ROLE = "restricted-role";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";
    private static final String PASSWORD = "password";

    @Spy
    private final ExecutorServiceBean executorServiceBean = ExecutorServiceBeenTestUtils.create(mock(ErrorReporterService.class));

    @Mock
    private AddRoleToAllProperties properties;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private RoleToAllService underTest;

    @Test
    void addToAll_restrictedRole() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.addToAll(USER_ID, PASSWORD, RESTRICTED_ROLE));
    }

    @Test
    void addToAll_nullPassword() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        Throwable ex = catchThrowable(() -> underTest.addToAll(USER_ID, null, ROLE));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    void addToAll() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        underTest.addToAll(USER_ID, PASSWORD, ROLE);

        then(checkPasswordService).should().checkPassword(USER_ID, PASSWORD);
        then(userDao).should(timeout(1000)).addRoleToAll(ROLE);
    }

    @Test
    void removeFromAll_restrictedRole() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.removeFromAll(USER_ID, PASSWORD, RESTRICTED_ROLE));
    }

    @Test
    void removeFromAll_nullPassword() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        Throwable ex = catchThrowable(() -> underTest.removeFromAll(USER_ID, null, ROLE));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    void removeFromAll() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        underTest.removeFromAll(USER_ID, PASSWORD, ROLE);

        then(checkPasswordService).should().checkPassword(USER_ID, PASSWORD);
        then(userDao).should(timeout(1000)).deleteRoleFromAll(ROLE);
    }
}