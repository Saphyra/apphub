package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.api.platform.authorization.client.AuthorizationClient;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.RoleRequestValidator;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class RoleRemovalServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID ADMIN_USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";
    public static final RoleRequest ROLE_REQUEST = RoleRequest.builder()
        .userId(USER_ID)
        .role(Role.TEST)
        .password(PASSWORD)
        .build();

    @Mock
    private RoleRequestValidator roleRequestValidator;

    @Mock
    private CheckPasswordService checkPasswordService;

    @Mock
    private UserDao userDao;

    @Mock
    private AuthorizationClient authorizationClient;

    @InjectMocks
    private RoleRemovalService underTest;

    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        given(userDao.findByUserIdValidated(USER_ID)).willReturn(user);
    }

    @AfterEach
    void verify() {
        then(roleRequestValidator).should().validate(ROLE_REQUEST);
        then(checkPasswordService).should().checkPassword(ADMIN_USER_ID, PASSWORD);
    }

    @Test
    public void roleNotFound() {
        given(user.getRoles()).willReturn(List.of());

        Throwable ex = catchThrowable(() -> underTest.removeRole(ADMIN_USER_ID, ROLE_REQUEST));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND);
    }

    @Test
    public void removeRole() {
        List<Role> roles = new ArrayList<>(List.of(Role.TEST));
        given(user.getRoles()).willReturn(roles);
        given(user.getUserId()).willReturn(USER_ID);

        assertThat(underTest.removeRole(ADMIN_USER_ID, ROLE_REQUEST)).isEqualTo(user);

        then(userDao).should().removeRole(USER_ID, Role.TEST);
        assertThat(roles.isEmpty()).isTrue();
        then(authorizationClient).should().invalidateAllAccessTokens(USER_ID);
    }
}