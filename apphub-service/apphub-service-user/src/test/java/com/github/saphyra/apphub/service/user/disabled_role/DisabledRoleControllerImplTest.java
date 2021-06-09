package com.github.saphyra.apphub.service.user.disabled_role;

import com.github.saphyra.apphub.api.user.model.response.DisabledRoleResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.user.common.CheckPasswordService;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleEntity;
import com.github.saphyra.apphub.service.user.disabled_role.dao.DisabledRoleRepository;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DisabledRoleControllerImplTest {
    private static final String ROLE = "role";
    private static final String ANOTHER_ROLE = "another-role";
    private static final String PASSWORD = "password";
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private DisabledRoleRepository repository;

    @Mock
    private DisabledRoleProperties properties;

    @Mock
    private CheckPasswordService checkPasswordService;

    @InjectMocks
    private DisabledRoleControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Before
    public void setUp() {
        given(properties.getRolesCanBeDisabled()).willReturn(Arrays.asList(ROLE, ANOTHER_ROLE));
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void disableRole_unknownRole() {
        Throwable ex = catchThrowable(() -> underTest.disableRole(new OneParamRequest<>(PASSWORD), "asd", accessTokenHeader));

        verifyException(ex, "unknown or cannot be disabled");
    }


    @Test
    public void disableRole() {
        underTest.disableRole(new OneParamRequest<>(PASSWORD), ROLE, accessTokenHeader);

        verify(repository).save(new DisabledRoleEntity(ROLE));
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
    }

    @Test
    public void enableRole_blank() {
        Throwable ex = catchThrowable(() -> underTest.enableRole(new OneParamRequest<>(PASSWORD), " ", accessTokenHeader));

        verifyException(ex, "must not be null or blank");
    }

    @Test
    public void enableRole() {
        given(repository.existsById(ROLE)).willReturn(true);

        underTest.enableRole(new OneParamRequest<>(PASSWORD), ROLE, accessTokenHeader);

        verify(repository).deleteById(ROLE);
        verify(checkPasswordService).checkPassword(USER_ID, PASSWORD);
    }

    @Test
    public void getDisabledRoles() {
        given(repository.findAll()).willReturn(Arrays.asList(new DisabledRoleEntity(ROLE)));

        List<DisabledRoleResponse> result = underTest.getDisabledRoles();

        assertThat(result).containsExactlyInAnyOrder(new DisabledRoleResponse(ROLE, true), new DisabledRoleResponse(ANOTHER_ROLE, false));
    }

    private void verifyException(Throwable ex, String message) {
        ExceptionValidator.validateInvalidParam(ex, "role", message);
    }
}