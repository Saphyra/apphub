package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetLabellessEventsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OWN_EVENT_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID SHARED_EVENT_ID = UUID.randomUUID();

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private GetLabellessEventsService underTest;

    @Mock
    private EventLabelMapping ownMapping;

    @Mock
    private EventLabelMapping sharedMapping;

    @Mock
    private Event ownEvent;

    @Mock
    private Event sharedEvent;

    @Mock
    private Alm alm;

    @Test
    void getLabellessEvents() {
        given(eventLabelMappingDao.getLabelsOfEventsByUserId(USER_ID)).willReturn(List.of(ownMapping));
        given(ownMapping.getLabelIds()).willReturn(Map.of());
        given(ownMapping.getEventId()).willReturn(OWN_EVENT_ID);
        given(eventDao.findByIdValidated(USER_ID, OWN_EVENT_ID)).willReturn(ownEvent);

        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.EVENT)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.SEE));
        given(alm.getOwner()).willReturn(OWNER);
        given(alm.getObjectId()).willReturn(SHARED_EVENT_ID);
        given(eventLabelMappingDao.getLabelsOfEvent(OWNER, SHARED_EVENT_ID)).willReturn(sharedMapping);
        given(sharedMapping.getLabelIds()).willReturn(Map.of());
        given(eventDao.findByIdValidated(OWNER, SHARED_EVENT_ID)).willReturn(sharedEvent);
        given(sharedEvent.setMasked(true)).willReturn(sharedEvent);

        assertThat(underTest.getLabellessEvents(USER_ID)).containsExactlyInAnyOrder(ownEvent, sharedEvent);
    }
}