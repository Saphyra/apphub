package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.request.RoleRequest;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RoleRemovalServiceTest {
    private static final String ROLE = "role";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private RoleDao roleDao;

    @Mock
    private RoleRequestValidator roleRequestValidator;

    @InjectMocks
    private RoleRemovalService underTest;

    @Mock
    private Role role;

    @Test
    public void roleNotFound() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.empty());
        RoleRequest request = RoleRequest.builder().userId(USER_ID).role(ROLE).build();

        Throwable ex = catchThrowable(() -> underTest.removeRole(request));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.ROLE_NOT_FOUND);
        verify(roleRequestValidator).validate(request);
    }

    @Test
    public void removeRole() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.of(role));
        RoleRequest request = RoleRequest.builder().userId(USER_ID).role(ROLE).build();

        underTest.removeRole(request);

        verify(roleDao).delete(role);

        verify(roleRequestValidator).validate(request);
    }
}