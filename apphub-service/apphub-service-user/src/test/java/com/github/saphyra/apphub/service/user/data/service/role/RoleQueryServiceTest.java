package com.github.saphyra.apphub.service.user.data.service.role;

import com.github.saphyra.apphub.api.user.model.role.UserRoleResponse;
import com.github.saphyra.apphub.service.user.data.dao.role.Role;
import com.github.saphyra.apphub.service.user.data.dao.role.RoleDao;
import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
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

    @Mock
    private RoleRequestValidator roleRequestValidator;

    @InjectMocks
    private RoleQueryService underTest;

    @Mock
    private User user;

    @Mock
    private Role role;

    @Test
    public void getRoles() {
        given(userDao.getByUsernameOrEmailContainingIgnoreCase(QUERY_STRING)).willReturn(Arrays.asList(user));
        given(user.getUserId()).willReturn(USER_ID);
        given(user.getEmail()).willReturn(EMAIL);
        given(user.getUsername()).willReturn(USERNAME);
        given(roleDao.getByUserId(USER_ID)).willReturn(Arrays.asList(role));
        given(role.getRole()).willReturn(ROLE);

        List<UserRoleResponse> result = underTest.getRoles(QUERY_STRING);

        then(roleRequestValidator).should().validateQuery(QUERY_STRING);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUserId()).isEqualTo(USER_ID);
        assertThat(result.get(0).getEmail()).isEqualTo(EMAIL);
        assertThat(result.get(0).getUsername()).isEqualTo(USERNAME);
        assertThat(result.get(0).getRoles()).containsExactly(ROLE);
    }

    @Test
    void getRolesForUser(){
        given(userDao.findByIdValidated(USER_ID)).willReturn(user);

        given(user.getUserId()).willReturn(USER_ID);
        given(user.getEmail()).willReturn(EMAIL);
        given(user.getUsername()).willReturn(USERNAME);
        given(roleDao.getByUserId(USER_ID)).willReturn(Arrays.asList(role));
        given(role.getRole()).willReturn(ROLE);

        assertThat(underTest.getRoles(USER_ID))
            .returns(USER_ID, UserRoleResponse::getUserId)
            .returns(EMAIL, UserRoleResponse::getEmail)
            .returns(USERNAME, UserRoleResponse::getUsername)
            .returns(List.of(ROLE), UserRoleResponse::getRoles);
    }
}