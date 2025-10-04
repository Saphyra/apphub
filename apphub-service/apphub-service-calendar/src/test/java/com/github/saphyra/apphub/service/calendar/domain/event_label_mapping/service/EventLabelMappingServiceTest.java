package com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.service;

import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.calendar.domain.event_label_mapping.dao.EventLabelMappingFactory;
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
class EventLabelMappingServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID_1 = UUID.randomUUID();
    private static final UUID LABEL_ID_2 = UUID.randomUUID();
    private static final UUID LABEL_ID_3 = UUID.randomUUID();

    @Mock
    private  EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private  EventLabelMappingFactory eventLabelMappingFactory;

    @Mock
    private  LabelIdValidator labelIdValidator;

    @InjectMocks
    private EventLabelMappingService underTest;

    @Mock
    private EventLabelMapping eventLabelMapping;

    @Mock
    private EventLabelMapping existingEventLabelMapping;

    @Mock
    private EventLabelMapping obsoleteEventLabelMapping;

    @Test
    void addLabels(){
        given(eventLabelMappingFactory.create(USER_ID, EVENT_ID, LABEL_ID_1)).willReturn(eventLabelMapping);

        underTest.addLabels(USER_ID, EVENT_ID, List.of(LABEL_ID_1));

        then(labelIdValidator).should().validate(List.of(LABEL_ID_1));
        then(eventLabelMappingDao).should().saveAll(List.of(eventLabelMapping));
    }

    @Test
    void hasLabel(){
        given(eventLabelMappingDao.getByEventId(EVENT_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getLabelId()).willReturn(LABEL_ID_1);

        assertThat(underTest.hasLabel(EVENT_ID, LABEL_ID_1)).isTrue();
    }

    @Test
    void getLabelIds(){
        given(eventLabelMappingDao.getByEventId(EVENT_ID)).willReturn(List.of(eventLabelMapping));
        given(eventLabelMapping.getLabelId()).willReturn(LABEL_ID_1);

        assertThat(underTest.getLabelIds(EVENT_ID)).containsExactly(LABEL_ID_1);
    }

    @Test

    void setLabels(){
        given(eventLabelMappingFactory.create(USER_ID, EVENT_ID, LABEL_ID_1)).willReturn(eventLabelMapping);
        given(eventLabelMappingDao.getByEventId(EVENT_ID)).willReturn(List.of(existingEventLabelMapping, obsoleteEventLabelMapping));
        given(existingEventLabelMapping.getLabelId()).willReturn(LABEL_ID_2);
        given(obsoleteEventLabelMapping.getLabelId()).willReturn(LABEL_ID_3);

        underTest.setLabels(USER_ID, EVENT_ID, List.of(LABEL_ID_1, LABEL_ID_2));

        then(labelIdValidator).should().validate(List.of(LABEL_ID_1, LABEL_ID_2));
        then(eventLabelMappingDao).should().delete(obsoleteEventLabelMapping);
        then(eventLabelMappingDao).should().save(eventLabelMapping);
    }
}