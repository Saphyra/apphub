package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.role.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class RoleAdditionServiceTest {
    private static final UUID ADMIN_USER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";
    private static final String PASSWORD = "password";
    public static final RoleRequest ROLE_REQUEST = RoleRequest.builder()
        .userId(USER_ID)
        .role(ROLE)
        .password(PASSWORD)
        .build();

    @Mock
    private RoleDao roleDao;

    @Mock
    private RoleFactory roleFactory;

    @Mock
    private RoleRequestValidator roleRequestValidator;

    @Mock
    private CheckPasswordService checkPasswordService;

    @InjectMocks
    private RoleAdditionService underTest;

    @Mock
    private Role role;

    @AfterEach
    void verifyCalls() {
        then(roleRequestValidator).should().validate(ROLE_REQUEST);
        then(checkPasswordService).should().checkPassword(ADMIN_USER_ID, PASSWORD);
    }

    @Test
    public void addRole_alreadyExists() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.of(role));

        Throwable ex = catchThrowable(() -> underTest.addRole(ADMIN_USER_ID, ROLE_REQUEST));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ROLE_ALREADY_EXISTS);
    }

    @Test
    public void addRole() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.empty());
        given(roleFactory.create(USER_ID, ROLE)).willReturn(role);

        underTest.addRole(ADMIN_USER_ID, ROLE_REQUEST);

        verify(roleDao).save(role);
    }
}