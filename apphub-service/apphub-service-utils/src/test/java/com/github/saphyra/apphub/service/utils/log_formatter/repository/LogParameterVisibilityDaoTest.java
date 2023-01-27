package com.github.saphyra.apphub.service.utils.log_formatter.repository;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LogParameterVisibilityDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID ID = UUID.randomUUID();
    private static final String ID_STRING = "id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LogParameterVisibilityConverter converter;

    @Mock
    private LogParameterVisibilityRepository repository;

    @InjectMocks
    private LogParameterVisibilityDao underTest;

    @Mock
    private LogParameterVisibilityEntity entity;

    @Mock
    private LogParameterVisibility domain;

    @Test
    public void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(Arrays.asList(entity));
        given(converter.convertEntity(Arrays.asList(entity))).willReturn(Arrays.asList(domain));

        List<LogParameterVisibility> result = underTest.getByUserId(USER_ID);

        assertThat(result).containsExactly(domain);
    }

    @Test
    public void findById() {
        given(uuidConverter.convertDomain(ID)).willReturn(ID_STRING);
        given(repository.findById(ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        Optional<LogParameterVisibility> result = underTest.findById(ID);

        assertThat(result).contains(domain);
    }
}