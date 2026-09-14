package com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class EventLabelMappingDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OTHER_USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OTHER_EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID OTHER_LABEL_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final String EVENT_ID_STRING = "event-id";
    private static final String OTHER_EVENT_ID_STRING = "other-event-id";

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private EventLabelMappingRepository repository;

    @Mock
    private EventLabelMappingConverter eventLabelMappingConverter;

    @Mock
    private EventLabelMappingCache eventLabelMappingCache;

    @InjectMocks
    private EventLabelMappingDao underTest;

    @Mock
    private EventLabelMapping mapping;

    @Mock
    private EventLabelMapping otherMapping;

    @Mock
    private EventLabelMapping modifiedMapping;

    @Mock
    private EventLabelMappingEntity entity;

    @Mock
    private EventLabelMappingEntity otherEntity;

    @Test
    void getLabelsOfEvents() {
        given(eventLabelMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, mapping));
        given(eventLabelMappingCache.get(eq(OTHER_USER_ID), any())).willReturn(Map.of(OTHER_EVENT_ID, otherMapping));

        assertThat(underTest.getLabelsOfEvents(List.of(new BiWrapper<>(USER_ID, EVENT_ID), new BiWrapper<>(OTHER_USER_ID, OTHER_EVENT_ID)))).containsExactly(mapping, otherMapping);
    }

    @Test
    void getLabelsOfEvent() {
        given(eventLabelMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, mapping));

        assertThat(underTest.getLabelsOfEvent(USER_ID, EVENT_ID)).isEqualTo(mapping);
    }

    @Test
    void getLabelsOfEvent_notFound() {
        given(eventLabelMappingCache.get(eq(USER_ID), any())).willReturn(Map.of());

        assertThat(underTest.getLabelsOfEvent(USER_ID, EVENT_ID))
            .returns(USER_ID, EventLabelMapping::getUserId)
            .returns(EVENT_ID, EventLabelMapping::getEventId)
            .returns(Map.of(), EventLabelMapping::getLabelIds);
    }

    @Test
    void getByUserId() {
        given(eventLabelMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, mapping));
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(eventLabelMappingConverter.convertEntity(List.of(entity))).willReturn(List.of(mapping));
        given(mapping.getEventId()).willReturn(EVENT_ID);

        assertThat(underTest.getByUserId(USER_ID)).containsEntry(EVENT_ID, mapping);

        ArgumentCaptor<Supplier<Map<UUID, EventLabelMapping>>> argumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        then(eventLabelMappingCache).should().get(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get()).containsEntry(EVENT_ID, mapping);
    }

    @Test
    void saveLabelsOfEvent() {
        given(mapping.getUserId()).willReturn(USER_ID);
        given(eventLabelMappingConverter.convertDomain(mapping)).willReturn(entity);

        underTest.saveLabelsOfEvent(mapping);

        then(repository).should().saveLabelsOfEvent(entity);
        then(eventLabelMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void deleteByEventId() {
        List<UUID> eventIds = List.of(EVENT_ID, OTHER_EVENT_ID);
        List<String> eventIdsString = List.of(EVENT_ID_STRING, OTHER_EVENT_ID_STRING);

        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(eventIds)).willReturn(eventIdsString);

        underTest.deleteByEventId(USER_ID, eventIds);

        then(repository).should().deleteLabelsOfEvents(USER_ID_STRING, eventIdsString);
        then(eventLabelMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void deleteByLabelId() {
        given(eventLabelMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(EVENT_ID, mapping, OTHER_EVENT_ID, otherMapping));
        given(mapping.getLabelIds()).willReturn(Map.of(LABEL_ID, USER_ID));
        given(otherMapping.getLabelIds()).willReturn(Map.of(OTHER_LABEL_ID, USER_ID));
        given(mapping.removeLabelId(LABEL_ID)).willReturn(modifiedMapping);
        given(eventLabelMappingConverter.convertDomain(modifiedMapping)).willReturn(entity);
        given(modifiedMapping.getUserId()).willReturn(USER_ID);

        underTest.deleteByLabelId(USER_ID, LABEL_ID);

        then(mapping).should().removeLabelId(LABEL_ID);
        then(repository).should().saveLabelsOfEvent(entity);
        then(eventLabelMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void invalidate() {
        underTest.invalidate(USER_ID);

        then(eventLabelMappingCache).should().invalidate(USER_ID);
    }
}