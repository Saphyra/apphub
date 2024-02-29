package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
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

@ExtendWith(MockitoExtension.class)
public class RoleRequestValidatorTest {
    private static final String ROLE = "role";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id-string";
    private static final String PASSWORD = "password";
    private static final String QUERY = "query";

    @Mock
    private UserDao userDao;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private RoleRequestValidator underTest;

    @Mock
    private User user;

    @Test
    public void nullQuery() {
        Throwable ex = catchThrowable(() -> underTest.validateQuery(null));

        ExceptionValidator.validateInvalidParam(ex, "query", "must not be null");
    }

    @Test
    public void queryTooShort() {

        Throwable ex = catchThrowable(() -> underTest.validateQuery("as"));

        ExceptionValidator.validateInvalidParam(ex, "query", "too short");
    }

    @Test
    void validateQuery(){
        underTest.validateQuery(QUERY);
    }

    @Test
    public void userIdNull() {
        RoleRequest request = RoleRequest.builder()
            .userId(null)
            .role(ROLE)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "userId", "must not be null");
    }

    @Test
    public void roleBlank() {
        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(" ")
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "role", "must not be null or blank");
    }

    @Test
    public void nullPassword() {
        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(ROLE)
            .password(null)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateInvalidParam(ex, "password", "must not be null");
    }

    @Test
    public void userNotFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(userDao.findById(USER_ID_STRING)).willReturn(Optional.empty());

        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(ROLE)
            .password(PASSWORD)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void valid() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(userDao.findById(USER_ID_STRING)).willReturn(Optional.of(user));

        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(ROLE)
            .password(PASSWORD)
            .build();

        underTest.validate(request);

        //No exception thrown
    }
}