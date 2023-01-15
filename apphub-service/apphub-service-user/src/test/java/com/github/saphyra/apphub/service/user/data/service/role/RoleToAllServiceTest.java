package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeanFactory;
import com.github.saphyra.apphub.lib.concurrency.ExecutorServiceBeenTestUtils;
import com.github.saphyra.apphub.lib.error_report.ErrorReporterService;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoleToAllServiceTest {
    private static final String RESTRICTED_ROLE = "restricted-role";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";
    private static final String PASSWORD = "password";

    @Spy
    private final ExecutorServiceBeanFactory executorServiceBeanFactory = ExecutorServiceBeenTestUtils.createFactory(Mockito.mock(ErrorReporterService.class));

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @Mock
    private RoleFactory roleFactory;

    @Mock
    private AddRoleToAllProperties properties;

    @Mock
    private CheckPasswordService checkPasswordService;

    @InjectMocks
    private RoleToAllService underTest;

    @Mock
    private User user;

    @Mock
    private Role role;


    @Test
    public void addToAll_restricted() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        Throwable ex = catchThrowable(() -> underTest.addToAll(USER_ID, PASSWORD, RESTRICTED_ROLE));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void nullPassword() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        Throwable ex = catchThrowable(() -> underTest.addToAll(USER_ID, null, ROLE));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void addToAll_alreadyHave() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));
        given(userDao.findAll()).willReturn(List.of(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.of(role));

        underTest.addToAll(USER_ID, PASSWORD, ROLE);

        verify(roleDao, timeout(1000)).saveAll(Collections.emptyList());
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
    }

    @Test
    public void addToAll() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));
        given(userDao.findAll()).willReturn(List.of(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.empty());
        given(roleFactory.create(USER_ID, ROLE)).willReturn(role);

        underTest.addToAll(USER_ID, PASSWORD, ROLE);

        verify(roleDao, timeout(1000)).saveAll(List.of(role));
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
    }

    @Test
    public void removeFromAll_restricted() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        Throwable ex = catchThrowable(() -> underTest.removeFromAll(USER_ID, PASSWORD, RESTRICTED_ROLE));

        ExceptionValidator.validateForbiddenOperation(ex);
    }

    @Test
    public void removeFromAll_nullPassword() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        Throwable ex = catchThrowable(() -> underTest.removeFromAll(USER_ID, null, ROLE));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void removeFromAll() {
        given(properties.getRestrictedRoles()).willReturn(List.of(RESTRICTED_ROLE));

        underTest.removeFromAll(USER_ID, PASSWORD, ROLE);

        verify(roleDao, timeout(1000)).deleteByRole(ROLE);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
    }
}