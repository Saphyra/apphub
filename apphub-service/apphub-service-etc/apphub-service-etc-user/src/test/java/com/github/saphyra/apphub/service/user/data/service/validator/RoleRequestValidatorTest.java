package com.github.saphyra.apphub.service.user.data.service.validator;

import com.github.saphyra.apphub.lib.common_domain.Role;
import com.github.saphyra.apphub.api.etc.user.model.role.RoleRequest;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;

@ExtendWith(MockitoExtension.class)
public class RoleRequestValidatorTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String PASSWORD = "password";

    @InjectMocks
    private RoleRequestValidator underTest;

    @Test
    public void userIdNull() {
        RoleRequest request = RoleRequest.builder()
            .userId(null)
            .role(Role.TEST)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "userId", "must not be null");
    }

    @Test
    public void roleNull() {
        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(null)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "role", "must not be null");
    }

    @Test
    public void nullPassword() {
        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(Role.TEST)
            .password(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void valid() {
        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(Role.TEST)
            .password(PASSWORD)
            .build();

        underTest.validate(request);
    }
}