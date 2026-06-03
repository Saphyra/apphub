package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventLabelMappingDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String LABEL_ID_STRING = "label-id";
    private static final String EVENT_ID_STRING = "event-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EventLabelMappingRepository repository;

    @InjectMocks
    private EventLabelMappingDao underTest;

    @Test
    void getEventsOfLabel() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.getEventsOfLabel(USER_ID_STRING, LABEL_ID_STRING)).willReturn(List.of(EVENT_ID_STRING));
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);

        assertThat(underTest.getEventsOfLabel(USER_ID, LABEL_ID)).containsExactly(EVENT_ID);
    }

    @Test
    void getLabelsOfEvents() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(List.of(EVENT_ID))).willReturn(List.of(EVENT_ID_STRING));
        given(repository.getLabelsOfEvents(USER_ID_STRING, List.of(EVENT_ID_STRING))).willReturn(List.of(new BiWrapper<>(EVENT_ID_STRING, List.of(LABEL_ID_STRING))));
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(List.of(LABEL_ID_STRING))).willReturn(List.of(LABEL_ID));

        assertThat(underTest.getLabelsOfEvents(USER_ID, List.of(EVENT_ID))).isEqualTo(Map.of(EVENT_ID, List.of(LABEL_ID)));
    }

    @Test
    void getLabelsOfEventsByUserId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getLabelsOfEventsByUserId(USER_ID_STRING)).willReturn(List.of(new BiWrapper<>(EVENT_ID_STRING, List.of(LABEL_ID_STRING))));
        given(uuidConverter.convertEntity(EVENT_ID_STRING)).willReturn(EVENT_ID);
        given(uuidConverter.convertEntity(List.of(LABEL_ID_STRING))).willReturn(List.of(LABEL_ID));

        assertThat(underTest.getLabelsOfEventsByUserId(USER_ID)).isEqualTo(Map.of(EVENT_ID, List.of(LABEL_ID)));
    }

    @Test
    void saveLabelsOfEvent(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(EVENT_ID)).willReturn(EVENT_ID_STRING);
        given(uuidConverter.convertDomain(List.of(LABEL_ID))).willReturn(List.of(LABEL_ID_STRING));

        underTest.saveLabelsOfEvent(USER_ID, EVENT_ID, List.of(LABEL_ID));

        then(repository).should().saveLabelsOfEvent(USER_ID_STRING, EVENT_ID_STRING, List.of(LABEL_ID_STRING));
    }

    @Test
    void deleteByEventId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(List.of(EVENT_ID))).willReturn(List.of(EVENT_ID_STRING));
        given(repository.getEventsOfLabelsByUserId(USER_ID_STRING)).willReturn(List.of(new BiWrapper<>(EVENT_ID_STRING, List.of(LABEL_ID_STRING))));
        given(repository.getEventsOfLabelsByUserId(USER_ID_STRING)).willReturn(List.of(new BiWrapper<>(LABEL_ID_STRING, CollectionUtils.toList(EVENT_ID_STRING))));

        underTest.deleteByEventId(USER_ID, List.of(EVENT_ID));

        then(repository).should().deleteLabelsOfEvents(USER_ID_STRING, List.of(EVENT_ID_STRING));
        then(repository).should().saveEventsOfLabels(USER_ID_STRING, List.of(new BiWrapper<>(LABEL_ID_STRING, List.of())));
    }

    @Test
    void deleteByLabelId(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(repository.getLabelsOfEventsByUserId(USER_ID_STRING)).willReturn(List.of(new BiWrapper<>(EVENT_ID_STRING, CollectionUtils.toList(LABEL_ID_STRING))));

        underTest.deleteByLabelId(USER_ID, LABEL_ID);

        then(repository).should().deleteEventsOfLabel(USER_ID_STRING, LABEL_ID_STRING);
        then(repository).should().saveEventsOfLabels(USER_ID_STRING, List.of(new BiWrapper<>(EVENT_ID_STRING, List.of())));
    }

    @Test
    void saveEventsOfLabel(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(uuidConverter.convertDomain(List.of(EVENT_ID))).willReturn(List.of(EVENT_ID_STRING));

        underTest.saveEventsOfLabel(USER_ID, LABEL_ID, List.of(EVENT_ID));

        then(repository).should().saveEventsOfLabels(USER_ID_STRING, LABEL_ID_STRING, List.of(EVENT_ID_STRING));
    }

    @Test
    void getEventsOfLabels(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(List.of(LABEL_ID))).willReturn(List.of(LABEL_ID_STRING));
        given(repository.getEventsOfLabels(USER_ID_STRING, List.of(LABEL_ID_STRING))).willReturn(List.of(new BiWrapper<>(LABEL_ID_STRING, List.of(EVENT_ID_STRING))));
        given(uuidConverter.convertEntity(LABEL_ID_STRING)).willReturn(LABEL_ID);
        given(uuidConverter.convertEntity(List.of(EVENT_ID_STRING))).willReturn(List.of(EVENT_ID));

        assertThat(underTest.getEventsOfLabels(USER_ID, List.of(LABEL_ID))).containsExactly(new BiWrapper<>(LABEL_ID, List.of(EVENT_ID)));
    }

    @Test
    void saveEventsOfLabels(){
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);
        given(uuidConverter.convertDomain(List.of(EVENT_ID))).willReturn(List.of(EVENT_ID_STRING));

        underTest.saveEventsOfLabels(USER_ID, List.of(new BiWrapper<>(LABEL_ID, List.of(EVENT_ID))));

        then(repository).should().saveEventsOfLabels(USER_ID_STRING, List.of(new BiWrapper<>(LABEL_ID_STRING, List.of(EVENT_ID_STRING))));
    }
}