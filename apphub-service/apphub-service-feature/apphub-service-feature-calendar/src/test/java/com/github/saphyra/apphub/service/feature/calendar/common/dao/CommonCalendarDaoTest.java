package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CommonCalendarDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OTHER_EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL = "label";
    private static final UUID OTHER_LABEL_ID = UUID.randomUUID();

    @Mock
    private UuidConverter uuidConverter;

    @Mock
    private CommonCalendarRepository commonCalendarRepository;

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private LabelDao labelDao;

    @Mock
    private AlmDao almDao;

    @Mock
    private LabelObjectQueryService labelObjectQueryService;

    @InjectMocks
    private CommonCalendarDao underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Test
    void deleteByUserId() {
        Label label = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(eventDao).should().delete(USER_ID, List.of(EVENT_ID));
        then(occurrenceDao).should().delete(List.of(occurrence));
        then(almDao).should().deleteByObject(OCCURRENCE_ID, SharedObjectType.OCCURRENCE);
        then(almDao).should().deleteByObject(EVENT_ID, SharedObjectType.EVENT);
        then(almDao).should().deleteByObject(LABEL_ID, SharedObjectType.LABEL);
        then(commonCalendarRepository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void saveNewEvent() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);

        underTest.saveNewEvent(event, List.of(occurrence), Map.of(LABEL_ID, USER_ID));

        then(eventDao).should().save(event);
        then(occurrenceDao).should().save(List.of(occurrence));
        then(eventLabelMappingDao).should().saveLabelsOfEvent(new EventLabelMapping(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID)));
    }

    @Test
    void deleteEvents() {
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));

        underTest.deleteEvents(USER_ID, List.of(EVENT_ID));

        then(eventDao).should().delete(USER_ID, List.of(EVENT_ID));
        then(occurrenceDao).should().delete(List.of(occurrence));
        then(almDao).should().deleteByObject(OCCURRENCE_ID, SharedObjectType.OCCURRENCE);
        then(almDao).should().deleteByObject(EVENT_ID, SharedObjectType.EVENT);
        then(eventLabelMappingDao).should().deleteByEventId(USER_ID, List.of(EVENT_ID));
    }

    @Test
    void deleteLabel() {
        underTest.deleteLabel(USER_ID, LABEL_ID);

        then(labelDao).should().delete(USER_ID, LABEL_ID);
        then(eventLabelMappingDao).should().deleteByLabelId(USER_ID, LABEL_ID);
        then(almDao).should().deleteByObject(LABEL_ID, SharedObjectType.LABEL);
    }

    @Test
    void saveLabel() {
        Label label = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        underTest.saveLabel(label);

        then(labelDao).should().save(label);
        then(eventLabelMappingDao).should().saveEventsOfLabel(new LabelEventMapping(USER_ID, LABEL_ID, Map.of()));
    }

    @Test
    void editLabelsOfEvent_addEventToLabel() {
        Label label = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        LabelEventMapping labelEventMapping = new LabelEventMapping(USER_ID, LABEL_ID, Map.of());
        given(labelObjectQueryService.getByUserId(USER_ID)).willReturn(Stream.of(new BiWrapper<>(label, null)));
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));

        underTest.editLabelsOfEvent(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID));

        then(eventLabelMappingDao).should().saveLabelsOfEvent(new EventLabelMapping(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID)));
        then(eventLabelMappingDao).should().saveEventsOfLabels(List.of(labelEventMapping));
        assertThat(labelEventMapping.getEventIds())
            .hasSize(1)
            .containsEntry(EVENT_ID, USER_ID);
    }

    @Test
    void editLabelsOfEvent_removeEventFromLabel() {
        Label label = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();
        given(labelObjectQueryService.getByUserId(USER_ID)).willReturn(Stream.of(new BiWrapper<>(label, null)));
        LabelEventMapping labelEventMapping = new LabelEventMapping(USER_ID, OTHER_LABEL_ID, Map.of(EVENT_ID, USER_ID, OTHER_EVENT_ID, USER_ID));
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));

        underTest.editLabelsOfEvent(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID));

        then(eventLabelMappingDao).should().saveLabelsOfEvent(new EventLabelMapping(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID)));
        then(eventLabelMappingDao).should().saveEventsOfLabels(List.of(labelEventMapping));
        assertThat(labelEventMapping.getEventIds())
            .hasSize(1)
            .containsEntry(OTHER_EVENT_ID, USER_ID);
    }

    @Test
    void editLabelsOfEvent_noModifiedMappings() {
        Label label = Label.builder()
            .userId(USER_ID)
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();
        given(labelObjectQueryService.getByUserId(USER_ID)).willReturn(Stream.of(new BiWrapper<>(label, null)));
        LabelEventMapping labelEventMapping = new LabelEventMapping(USER_ID, LABEL_ID, Map.of(EVENT_ID, USER_ID));
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));

        underTest.editLabelsOfEvent(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID));

        then(eventLabelMappingDao).should().saveLabelsOfEvent(new EventLabelMapping(USER_ID, EVENT_ID, Map.of(LABEL_ID, USER_ID)));
        then(eventLabelMappingDao).should(never()).saveEventsOfLabels(any());
    }
}