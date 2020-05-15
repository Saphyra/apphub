package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.service.user.data.dao.user.User;
import com.github.saphyra.apphub.service.user.data.dao.user.UserConverter;
import com.github.saphyra.apphub.service.user.data.dao.user.UserDao;
import com.github.saphyra.apphub.service.user.data.dao.user.UserEntity;
import com.github.saphyra.apphub.service.user.data.dao.user.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class UserDaoTest {
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";

    @Mock
    private UserRepository repository;

    @Mock
    private UserConverter converter;

    @InjectMocks
    private UserDao underTest;

    @Mock
    private UserEntity entity;

    @Mock
    private User user;

    @Test
    public void findByEmail() {
        given(repository.findByEmail(EMAIL)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(user));

        Optional<User> result = underTest.findByEmail(EMAIL);

        assertThat(result).contains(user);
    }

    @Test
    public void findByUsername() {
        given(repository.findByUsername(USERNAME)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(user));

        Optional<User> result = underTest.findByUsername(USERNAME);

        assertThat(result).contains(user);
    }
}