package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventLabelMappingDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String EVENT_ID_STRING = "event-id";
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL_ID_STRING = "label-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EventLabelMappingConverter converter;

    @Mock
    private EventLabelMappingRepository repository;

    @InjectMocks
    private EventLabelMappingDao underTest;

    @Mock
    private EventLabelMappingEntity entity;

    @Mock
    private EventLabelMapping domain;

    @Test
    void deleteByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(repository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void deleteByUserIdAndEventId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);

        underTest.deleteByUserIdAndEventId(USER_ID, EVENT_ID);

        then(repository).should().deleteByUserIdAndEventId(USER_ID_STRING, EVENT_ID_STRING);
    }

    @Test
    void getByEventId(){
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(repository.getByEventId(EVENT_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByEventId(EVENT_ID)).containsExactly(domain);
    }

    @Test
    void deleteByUserIdAndLabelId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        underTest.deleteByUserIdAndLabelId(USER_ID, LABEL_ID);

        then(repository).should().deleteByUserIdAndLabelId(USER_ID_STRING, LABEL_ID_STRING);
    }

    @Test
    void getByUserIdAndLabelId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.getByUserIdAndLabelId(USER_ID_STRING, LABEL_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(domain));

        assertThat(underTest.getByUserIdAndLabelId(USER_ID, LABEL_ID)).containsExactly(domain);
    }
}