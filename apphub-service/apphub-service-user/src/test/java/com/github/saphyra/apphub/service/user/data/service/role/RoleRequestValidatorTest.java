package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_util.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.lib.exception.BadRequestException;
import com.github.saphyra.apphub.lib.exception.NotFoundException;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class RoleRequestValidatorTest {
    private static final String ROLE = "role";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id-string";

    @Mock
    private UserDao userDao;

    @Mock
    private UuidConverter uuidConverter;

    @InjectMocks
    private RoleRequestValidator underTest;

    @Mock
    private User user;

    @Test
    public void userIdNull() {
        RoleRequest request = RoleRequest.builder()
            .userId(null)
            .role(ROLE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("userId")).isEqualTo("must not be null");
    }

    @Test
    public void roleBlank() {
        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(" ")
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(BadRequestException.class);
        BadRequestException exception = (BadRequestException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(exception.getErrorMessage().getParams().get("role")).isEqualTo("must not be null or blank");
    }

    @Test
    public void userNotFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(userDao.findById(USER_ID_STRING)).willReturn(Optional.empty());

        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(ROLE)
            .build();

        Throwable ex = catchThrowable(() -> underTest.validate(request));

        assertThat(ex).isInstanceOf(NotFoundException.class);
        NotFoundException exception = (NotFoundException) ex;
        assertThat(exception.getErrorMessage().getErrorCode()).isEqualTo(ErrorCode.USER_NOT_FOUND.name());
    }

    @Test
    public void valid() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(userDao.findById(USER_ID_STRING)).willReturn(Optional.of(user));

        RoleRequest request = RoleRequest.builder()
            .userId(USER_ID)
            .role(ROLE)
            .build();

        underTest.validate(request);

        //No exception thrown
    }
}