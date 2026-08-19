package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindEventForOperationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @Mock
    private AlmDao almDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private EventGrantChecker eventGrantChecker;

    @InjectMocks
    private FindEventForOperationService underTest;

    @Mock
    private Event event;

    @Mock
    private Alm eventAlm;

    @Mock
    private Alm labelAlm;

    @Mock
    private LabelEventMapping labelEventMapping;

    @Test
    void ownEvent() {
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));

        assertThat(underTest.findEvent(USER_ID, EVENT_ID, Operation.EDIT)).contains(event);
    }

    @Test
    void sharedEventFound() {
        given(eventDao.findById(SHARED_WITH, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(eventAlm));
        given(eventAlm.getOwner()).willReturn(USER_ID);
        given(eventAlm.getObjectId()).willReturn(EVENT_ID);
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));
        given(eventGrantChecker.hasGrants(SHARED_WITH, event, Operation.EDIT.getRequiredGrants())).willReturn(true);

        assertThat(underTest.findEvent(SHARED_WITH, EVENT_ID, Operation.EDIT)).contains(event);
    }

    @Test
    void sharedLabelFound() {
        given(eventDao.findById(SHARED_WITH, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of(labelAlm));
        given(labelAlm.getOwner()).willReturn(USER_ID);
        given(labelAlm.getObjectId()).willReturn(LABEL_ID);
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));
        given(labelEventMapping.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));
        given(eventGrantChecker.hasGrants(SHARED_WITH, event, Operation.EDIT.getRequiredGrants())).willReturn(true);

        assertThat(underTest.findEvent(SHARED_WITH, EVENT_ID, Operation.EDIT)).contains(event);
    }
}