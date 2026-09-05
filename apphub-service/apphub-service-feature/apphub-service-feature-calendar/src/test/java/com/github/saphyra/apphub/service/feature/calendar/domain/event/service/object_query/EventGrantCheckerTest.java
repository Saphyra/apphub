package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventGrantCheckerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @InjectMocks
    private EventGrantChecker underTest;

    @Mock
    private Event event;

    @Mock
    private Alm alm1;

    @Mock
    private Alm alm2;

    @Mock
    private EventLabelMapping eventLabelMapping;

    @Test
    void ownEvent() {
        given(event.getUserId()).willReturn(USER_ID);

        assertThat(underTest.hasGrants(USER_ID, event, List.of(Grant.DELETE))).isTrue();
    }

    @Test
    void sharedEventContainsAllGrants() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm1));
        given(alm1.getGrants()).willReturn(Set.of(Grant.DELETE));

        assertThat(underTest.hasGrants(SHARED_WITH, event, List.of(Grant.DELETE))).isTrue();
    }

    @Test
    void sharedLabelContainsAllGrants() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());
        given(eventLabelMappingDao.getLabelsOfEvent(USER_ID, EVENT_ID)).willReturn(eventLabelMapping);
        given(eventLabelMapping.getLabelIds()).willReturn(Map.of(LABEL_ID, USER_ID));
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm1));
        given(alm1.getGrants()).willReturn(Set.of(Grant.DELETE_CHILDREN));

        assertThat(underTest.hasGrants(SHARED_WITH, event, List.of(Grant.DELETE))).isTrue();
    }

    @Test
    void requiredGrantMissing() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());
        given(eventLabelMappingDao.getLabelsOfEvent(USER_ID, EVENT_ID)).willReturn(eventLabelMapping);
        given(eventLabelMapping.getLabelIds()).willReturn(Map.of(LABEL_ID, USER_ID));
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm1));
        given(alm1.getGrants()).willReturn(Set.of(Grant.DELETE_CHILDREN));

        assertThat(underTest.hasGrants(SHARED_WITH, event, List.of(Grant.DELETE, Grant.VIEW))).isFalse();
    }

    @Test
    void legoGrants() {
        given(event.getUserId()).willReturn(USER_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm2));
        given(alm2.getGrants()).willReturn(Set.of(Grant.VIEW));
        given(eventLabelMappingDao.getLabelsOfEvent(USER_ID, EVENT_ID)).willReturn(eventLabelMapping);
        given(eventLabelMapping.getLabelIds()).willReturn(Map.of(LABEL_ID, USER_ID));
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm1));
        given(alm1.getGrants()).willReturn(Set.of(Grant.DELETE_CHILDREN));

        assertThat(underTest.hasGrants(SHARED_WITH, event, List.of(Grant.DELETE, Grant.VIEW))).isTrue();
    }
}