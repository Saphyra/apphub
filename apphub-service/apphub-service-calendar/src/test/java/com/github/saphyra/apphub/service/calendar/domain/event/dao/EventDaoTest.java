package com.github.saphyra.apphub.service.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class EventDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EventRepository repository;

    @Mock
    private EventConverter converter;

    @InjectMocks
    private EventDao underTest;

    @Mock
    private EventEntity entity;

    @Mock
    private Event domain;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserId(USER_ID)).isEqualTo(List.of(domain));
    }

    @Test
    void deleteByUserIdAndEventId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);

        underTest.deleteByUserIdAndEventId(USER_ID, EVENT_ID);

        then(repository).should().deleteByUserIdAndEventId(USER_ID_STRING, EVENT_ID_STRING);
    }

    @Test
    void findByIdValidated_notFound() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(repository.findById(EVENT_ID_STRING)).willReturn(Optional.empty());

        ExceptionValidator.validateNotFoundException(() -> underTest.findByIdValidated(EVENT_ID));
    }

    @Test
    void findByIdValidated() {
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(repository.findById(EVENT_ID_STRING)).willReturn(Optional.of(entity));
        given(converter.convertEntity(Optional.of(entity))).willReturn(Optional.of(domain));

        assertThat(underTest.findByIdValidated(EVENT_ID)).isEqualTo(domain);
    }
}