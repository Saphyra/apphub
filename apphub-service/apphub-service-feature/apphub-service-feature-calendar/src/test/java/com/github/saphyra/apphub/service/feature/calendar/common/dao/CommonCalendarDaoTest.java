package com.github.saphyra.apphub.service.feature.calendar.common.dao;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.converter.UuidConverter;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.LabelDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CommonCalendarDaoTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String USER_ID_STRING = "user-id";
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OTHER_EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final String LABEL = "label";

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

    @InjectMocks
    private CommonCalendarDao underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Test
    void deleteByUserId() {
        given(event.getEventId()).willReturn(EVENT_ID);
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(uuidConverter.convertDomain(USER_ID)).willReturn(USER_ID_STRING);

        underTest.deleteByUserId(USER_ID);

        then(occurrenceDao).should().deleteByEventId(EVENT_ID);
        then(eventDao).should().delete(USER_ID, List.of(EVENT_ID));
        then(commonCalendarRepository).should().deleteByUserId(USER_ID_STRING);
    }

    @Test
    void saveNewEvent() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);

        underTest.saveNewEvent(event, List.of(occurrence), List.of(LABEL_ID));

        then(eventDao).should().save(event);
        then(occurrenceDao).should().save(List.of(occurrence));
        then(eventLabelMappingDao).should().saveLabelsOfEvent(USER_ID, EVENT_ID, List.of(LABEL_ID));
    }

    @Test
    void deleteEvents() {
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));

        underTest.deleteEvents(USER_ID, List.of(EVENT_ID));

        then(eventDao).should().delete(USER_ID, List.of(EVENT_ID));
        then(occurrenceDao).should().delete(List.of(occurrence));
        then(eventLabelMappingDao).should().deleteByEventId(USER_ID, List.of(EVENT_ID));
    }

    @Test
    void deleteLabel() {
        underTest.deleteLabel(USER_ID, LABEL_ID);

        then(labelDao).should().delete(USER_ID, LABEL_ID);
        then(eventLabelMappingDao).should().deleteByLabelId(USER_ID, LABEL_ID);
    }

    @Test
    void saveLabel() {
        Label label = Label.builder()
            .labelId(LABEL_ID)
            .label(LABEL)
            .build();

        underTest.saveLabel(USER_ID, label);

        then(labelDao).should().save(USER_ID, label);
        then(eventLabelMappingDao).should().saveEventsOfLabel(USER_ID, LABEL_ID, List.of());
    }

    @Test
    void editLabelsOfEvent_addEventToLabel() {
        Label label = Label.builder().labelId(LABEL_ID).label(LABEL).build();
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(eventLabelMappingDao.getEventsOfLabels(USER_ID, List.of(LABEL_ID))).willReturn(List.of(new BiWrapper<>(LABEL_ID, List.of(OTHER_EVENT_ID))));

        underTest.editLabelsOfEvent(USER_ID, EVENT_ID, List.of(LABEL_ID));

        then(eventLabelMappingDao).should().saveLabelsOfEvent(USER_ID, EVENT_ID, List.of(LABEL_ID));
        then(eventLabelMappingDao).should().saveEventsOfLabels(USER_ID, List.of(new BiWrapper<>(LABEL_ID, List.of(OTHER_EVENT_ID, EVENT_ID))));

    }

    @Test
    void editLabelsOfEvent_removeEventFromLabel() {
        Label label = Label.builder().labelId(LABEL_ID).label(LABEL).build();
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(eventLabelMappingDao.getEventsOfLabels(USER_ID, List.of(LABEL_ID))).willReturn(List.of(new BiWrapper<>(LABEL_ID, List.of(EVENT_ID, OTHER_EVENT_ID))));

        underTest.editLabelsOfEvent(USER_ID, EVENT_ID, List.of());

        then(eventLabelMappingDao).should().saveLabelsOfEvent(USER_ID, EVENT_ID, List.of());
        then(eventLabelMappingDao).should().saveEventsOfLabels(USER_ID, List.of(new BiWrapper<>(LABEL_ID, List.of(OTHER_EVENT_ID))));

    }

    @Test
    void editLabelsOfEvent_noModifiedMappings() {
        Label label = Label.builder().labelId(LABEL_ID).label(LABEL).build();
        given(labelDao.getByUserId(USER_ID)).willReturn(List.of(label));
        given(eventLabelMappingDao.getEventsOfLabels(USER_ID, List.of(LABEL_ID))).willReturn(List.of(new BiWrapper<>(LABEL_ID, List.of(EVENT_ID, OTHER_EVENT_ID))));

        underTest.editLabelsOfEvent(USER_ID, EVENT_ID, List.of(LABEL_ID));

        then(eventLabelMappingDao).should().saveLabelsOfEvent(USER_ID, EVENT_ID, List.of(LABEL_ID));
        then(eventLabelMappingDao).should(never()).saveEventsOfLabels(any(), anyList());
    }
}