package com.github.saphyra.apphub.service.feature.calendar.domain.event.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_domain.ErrorCode;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";

    @Mock
    private EventRepository repository;

    @Mock
    private EventConverter converter;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EventCache eventCache;

    @InjectMocks
    private EventDao underTest;

    @Mock
    private EventEntity entity;

    @Mock
    private Event domain;

    @Test
    void findByIdValidated_notFound() {
        given(eventCache.get(eq(USER_ID), any())).willReturn(Map.of());

        Throwable ex = catchThrowable(() -> underTest.findByIdValidated(USER_ID, EVENT_ID));

        ExceptionValidator.validateNotLoggedException(ex, HttpStatus.NOT_FOUND, ErrorCode.DATA_NOT_FOUND);
    }

    @Test
    void findByIdValidated() {
        given(eventCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, domain));

        assertThat(underTest.findByIdValidated(USER_ID, EVENT_ID)).isEqualTo(domain);
    }

    @Test
    void getByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));
        given(domain.getEventId()).willReturn(EVENT_ID);
        given(eventCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, domain));

        assertThat(underTest.getByUserId(USER_ID)).containsEntry(EVENT_ID, domain);

        ArgumentCaptor<Supplier<Map<UUID, Event>>> argumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        then(eventCache).should().get(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get()).containsEntry(EVENT_ID, domain);
    }

    @Test
    void save() {
        given(domain.getUserId()).willReturn(USER_ID);
        given(converter.convertDomain(domain)).willReturn(entity);

        underTest.save(domain);

        then(repository).should().save(entity);
        then(eventCache).should().invalidate(USER_ID);
    }

    @Test
    void delete() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(List.of(EVENT_ID))).willReturn(List.of(EVENT_ID_STRING));

        underTest.delete(USER_ID, List.of(EVENT_ID));

        then(repository).should().delete(USER_ID_STRING, List.of(EVENT_ID_STRING));
        then(eventCache).should().invalidate(USER_ID);
    }

    @Test
    void getByIds_bwList() {
        given(eventCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, domain));

        assertThat(underTest.getByIds(List.of(new BiWrapper<>(USER_ID, EVENT_ID)))).containsExactly(domain);
    }

    @Test
    void invalidate() {
        underTest.invalidate(USER_ID);

        then(eventCache).should().invalidate(USER_ID);
    }
}