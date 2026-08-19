package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class GetEventsServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @Mock
    private AlmDao almDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @InjectMocks
    private GetEventsService underTest;

    @Mock
    private Event ownEvent;

    @Mock
    private Event sharedEvent;

    @Mock
    private Alm eventAlm;

    @Mock
    private Alm labelAlm;

    @Mock
    private LabelEventMapping labelEventMapping;

    @Test
    void getEvents() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(ownEvent));
        given(ownEvent.setMasked(false)).willReturn(ownEvent);

        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.EVENT)).willReturn(List.of(eventAlm));
        given(eventAlm.getGrants()).willReturn(Set.of(Grant.SEE));
        given(eventAlm.getOwner()).willReturn(OWNER);
        given(eventAlm.getObjectId()).willReturn(EVENT_ID);
        given(eventDao.findByIdValidated(OWNER, EVENT_ID)).willReturn(sharedEvent);

        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.LABEL)).willReturn(List.of(labelAlm));
        given(labelAlm.getOwner()).willReturn(OWNER);
        given(labelAlm.getObjectId()).willReturn(LABEL_ID);
        given(eventLabelMappingDao.getEventsOfLabel(OWNER, LABEL_ID)).willReturn(Optional.of(labelEventMapping));
        given(labelEventMapping.getEventIds()).willReturn(Map.of(EVENT_ID, OWNER));
        given(sharedEvent.getUserId()).willReturn(OWNER);
        given(labelAlm.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));
        given(sharedEvent.setMasked(false)).willReturn(sharedEvent);

        assertThat(underTest.getEvents(USER_ID)).containsExactlyInAnyOrder(ownEvent, sharedEvent);
    }
}