package com.github.saphyra.apphub.service.feature.calendar.domain.label_event_mapping.dao;

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
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class LabelEventMappingDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OTHER_LABEL_ID = UUID.randomUUID();
    private static final String LABEL_ID_STRING = "label-id";

    @Mock
    private LabelEventMappingConverter converter;

    @Mock
    private LabelEventMappingRepository repository;

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private LabelEventMappingCache labelEventMappingCache;

    @InjectMocks
    private LabelEventMappingDao underTest;

    @Mock
    private LabelEventMapping mapping;

    @Mock
    private LabelEventMappingEntity entity;

    @Mock
    private LabelEventMapping otherMapping;

    @Test
    void getByUserId() {
        given(labelEventMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, mapping));
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(repository.getByUserId(USER_ID_STRING)).willReturn(List.of(entity));
        given(converter.convertEntity(List.of(entity))).willReturn(List.of(mapping));
        given(mapping.getLabelId()).willReturn(LABEL_ID);

        assertThat(underTest.getByUserId(USER_ID)).containsEntry(LABEL_ID, mapping);

        ArgumentCaptor<Supplier<Map<UUID, LabelEventMapping>>> argumentCaptor = ArgumentCaptor.forClass(Supplier.class);
        then(labelEventMappingCache).should().get(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue().get()).containsEntry(LABEL_ID, mapping);
    }

    @Test
    void getEventsOfLabel() {
        given(labelEventMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, mapping));

        assertThat(underTest.getEventsOfLabel(USER_ID, LABEL_ID)).contains(mapping);
    }

    @Test
    void saveEventsOfLabel() {
        given(converter.convertDomain(mapping)).willReturn(entity);
        given(mapping.getUserId()).willReturn(USER_ID);

        underTest.saveEventsOfLabel(mapping);

        then(repository).should().save(entity);
        then(labelEventMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void saveEventsOfLabels() {
        given(converter.convertDomain(List.of(mapping))).willReturn(List.of(entity));
        given(mapping.getUserId()).willReturn(USER_ID);

        underTest.saveEventsOfLabels(List.of(mapping));

        then(repository).should().save(List.of(entity));
        then(labelEventMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void deleteByEventId() {
        given(labelEventMappingCache.get(eq(USER_ID), any())).willReturn(Map.of(LABEL_ID, mapping, OTHER_LABEL_ID, otherMapping));
        given(mapping.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(otherMapping.getEventIds()).willReturn(Map.of(UUID.randomUUID(), USER_ID));
        given(mapping.getUserId()).willReturn(USER_ID);
        given(converter.convertDomain(mapping)).willReturn(entity);

        underTest.deleteByEventId(USER_ID, List.of(EVENT_ID));

        then(mapping).should().removeEvent(EVENT_ID);
        then(repository).should().save(entity);
        then(labelEventMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void deleteByLabelId() {
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);
        given(uuidConverter.convertDomain(LABEL_ID)).willReturn(LABEL_ID_STRING);

        underTest.deleteByLabelId(USER_ID, LABEL_ID);

        then(repository).should().delete(USER_ID_STRING, LABEL_ID_STRING);
        then(labelEventMappingCache).should().invalidate(USER_ID);
    }

    @Test
    void invalidate() {
        underTest.invalidate(USER_ID);

        then(labelEventMappingCache).should().invalidate(USER_ID);
    }
}