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
public class RoleAdditionServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String ROLE = "role";

    @Mock
    private RoleDao roleDao;

    @Mock
    private RoleFactory roleFactory;

    @Mock
    private RoleRequestValidator roleRequestValidator;

    @InjectMocks
    private RoleAdditionService underTest;

    @Mock
    private Role role;

    @Test
    public void addRole_alreadyExists() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.of(role));
        RoleRequest roleRequest = RoleRequest.builder().userId(USER_ID).role(ROLE).build();

        Throwable ex = catchThrowable(() -> underTest.addRole(roleRequest));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.CONFLICT, ErrorCode.ROLE_ALREADY_EXISTS);
        verify(roleRequestValidator).validate(roleRequest);
    }

    @Test
    public void addRole() {
        given(roleDao.findByUserIdAndRole(USER_ID, ROLE)).willReturn(Optional.empty());
        given(roleFactory.create(USER_ID, ROLE)).willReturn(role);
        RoleRequest roleRequest = RoleRequest.builder().userId(USER_ID).role(ROLE).build();

        underTest.addRole(RoleRequest.builder().userId(USER_ID).role(ROLE).build());

        verify(roleDao).save(role);

        verify(roleRequestValidator).validate(roleRequest);
    }
}