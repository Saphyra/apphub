package com.github.saphyra.apphub.service.user.data.dao.user;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserDaoTest {
    private static final String EMAIL = "email";
    private static final String USERNAME = "username";
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String QUERY_STRING = "query-string";
    public static final int MAX_NUMBER_OF_USERS_FOUND = 100;
    private static final String USER_IDENTIFIER = "user-identifier";

    @Mock
    private UserRepository repository;

    @Mock
    private UserConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    private UserDao underTest;

    @Mock
    private UserEntity entity;

    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        underTest = UserDao.builder()
            .converter(converter)
            .repository(repository)
            .uuidConverter(uuidConverter)
            .maxNumberOfUsersFound(MAX_NUMBER_OF_USERS_FOUND)
            .build();
    }

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

    @Test
    public void findById_notFound() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(USER_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.USER_NOT_FOUND);
    }

    @Test
    public void findByIdValidated() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(user));

        User result = underTest.findByIdValidated(USER_ID);

        assertThat(result).isEqualTo(user);
    }

    @Test
    public void deleteById() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.existsById(USER_ID_STRING)).willReturn(true);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteById(USER_ID_STRING);
    }

    @Test
    public void getByUsernameOrEmailContainingIgnoreCase() {
        given(repository.getByUsernameOrEmailContainingIgnoreCase(QUERY_STRING, PageRequest.of(0, MAX_NUMBER_OF_USERS_FOUND))).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(user));

        List<User> result = underTest.getByUsernameOrEmailContainingIgnoreCase(QUERY_STRING);

        assertThat(result).containsExactly(user);
    }

    @Test
    public void getUsersMarkedToDelete() {
        given(repository.getByUsersMarkedToDelete()).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(user));

        List<User> result = underTest.getUsersMarkedToDelete();

        assertThat(result).containsExactly(user);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.findById(USER_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(user));

        Optional<User> result = underTest.findById(USER_ID);

        assertThat(result).contains(user);
    }

    @Test
    void findByUsernameOrEmail() {
        given(repository.findByUsernameOrEmail(USER_IDENTIFIER)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(user));

        assertThat(underTest.findByUsernameOrEmail(USER_IDENTIFIER)).contains(user);
    }
}