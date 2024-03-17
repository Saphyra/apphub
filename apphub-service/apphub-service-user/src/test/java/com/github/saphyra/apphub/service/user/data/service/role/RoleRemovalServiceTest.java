package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
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

@ExtendWith(MockitoExtension.class)
public class RoleRemovalServiceTest {
    private static final String ROLE = "role";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ADMIN_USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    public static final RoleRequest ROLE_REQUEST = RoleRequest.builder()
        .userId(USER_ID)
        .role(ROLE)
        .password(PASSWORD)
        .build();

    @Mock
    private RoleDao roleDao;

    @Mock
    private RoleRequestValidator roleRequestValidator;

    @Mock
    private CheckPasswordService checkPasswordService;

    @InjectMocks
    private RoleRemovalService underTest;

    @Mock
    private Role role;

    @AfterEach
    void verify() {
        then(roleRequestValidator).should().validate(ROLE_REQUEST);
        then(checkPasswordService).should().checkPassword(ADMIN_USER_ID, PASSWORD);
    }

    @Test
    public void roleNotFound() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.removeRole(ADMIN_USER_ID, ROLE_REQUEST));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND);
    }

    @Test
    public void removeRole() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.of(role));

        underTest.removeRole(ADMIN_USER_ID, ROLE_REQUEST);

        then(roleDao).should().delete(role);
    }
}