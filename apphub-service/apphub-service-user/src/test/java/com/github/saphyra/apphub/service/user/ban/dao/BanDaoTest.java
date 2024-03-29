package com.github.saphyra.apphub.service.user.ban.dao;

import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BanDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID BAN_ID = UUID.randomUUID();
    private static final String BAN_ID_STRING = "ban-id";
    private static final LocalDateTime EXPIRATION = LocalDateTime.now();

    @Mock
    private BanConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private BanRepository repository;

    @InjectMocks
    private BanDao underTest;

    @Mock
    private BanEntity entity;

    @Mock
    private Ban domain;

    @Test
    public void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        verify(repository).deleteByUserId(USER_ID_STRING);
    }

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));

        List<Ban> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void deleteExpired() {
        underTest.deleteExpired(EXPIRATION);

        verify(repository).deleteExpired(EXPIRATION);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(BAN_ID)).willReturn(BAN_ID_STRING);
        given(repository.findById(BAN_ID_STRING)).willReturn(Optional.empty());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(BAN_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(BAN_ID)).willReturn(BAN_ID_STRING);
        given(repository.findById(BAN_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(BAN_ID)).isEqualTo(domain);
    }
}