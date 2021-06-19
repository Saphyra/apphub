package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.response.UserRoleResponse;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
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

@RunWith(MockitoJUnitRunner.class)
public class RoleQueryServiceTest {
    private static final String QUERY_STRING = "query-string";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final String ROLE = "role";

    @Mock
    private RoleDao roleDao;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private RoleQueryService underTest;

    @Mock
    private User user;

    @Mock
    private Role role;

    @Test
    public void nullQueryString() {
        Throwable ex = catchThrowable(() -> underTest.getRoles(null));

        ExceptionValidator.validateInvalidParam(ex, "searchText", "must not be null");
    }

    @Test
    public void tooShortQueryString() {
        Throwable ex = catchThrowable(() -> underTest.getRoles("as"));

        ExceptionValidator.validateInvalidParam(ex, "searchText", "too short");
    }

    @Test
    public void getRoles() {
        given(userDao.getByUsernameOrEmailContainingIgnoreCase(QUERY_STRING)).willReturn(Arrays.asList(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(user.getEmail()).willReturn(EMAIL);
        given(user.getUsername()).willReturn(USERNAME);
        given(roleDao.getByUserId(USER_ID)).willReturn(Arrays.asList(role));
        given(role.getRole()).willReturn(ROLE);

        List<UserRoleResponse> result = underTest.getRoles(QUERY_STRING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(USER_ID);
        assertThat(result.get(0).getEmail()).isEqualTo(EMAIL);
        assertThat(result.get(0).getUsername()).isEqualTo(USERNAME);
        assertThat(result.get(0).getRoles()).containsExactly(ROLE);
    }
}