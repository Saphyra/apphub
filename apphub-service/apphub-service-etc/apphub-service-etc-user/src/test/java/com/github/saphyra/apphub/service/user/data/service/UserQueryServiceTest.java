package com.github.saphyra.apphub.service.user.data.service;

import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.service.validator.UserQueryValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {
    private static final String QUERY_STRING = "query-string";

    @Mock
    private UserQueryValidator userQueryValidator;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private UserQueryService underTest;

    @Mock
    private User user;

    @Test
    public void getUsers() {
        given(userDao.findByUserIdentifier(QUERY_STRING)).willReturn(Optional.of(user));

        assertThat(underTest.getUsers(QUERY_STRING)).containsExactly(user);

        then(userQueryValidator).should().validateQuery(QUERY_STRING);
    }
}