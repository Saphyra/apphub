package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.service.LabelObjectQueryService;
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
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventMaskedCheckerTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private LabelObjectQueryService labelObjectQueryService;

    @InjectMocks
    private EventMaskedChecker underTest;

    @Mock
    private Event event;

    @Mock
    private Alm alm1;

    @Mock
    private Alm alm2;

    @Mock
    private LabelEventMapping labelEventMapping;

    @Test
    void isMasked_labelAlm_ownEvent() {
        given(event.getUserId()).willReturn(USER_ID);

        assertThat(underTest.isMasked_labelAlm(USER_ID, event, alm1, LABEL_ID)).isFalse();
    }

    @Test
    void isMasked_labelAlm_nullAlm_labelAvailable_hasViewGrant() {
        given(labelObjectQueryService.findLabel(SHARED_WITH, LABEL_ID)).willReturn(Optional.of(new BiWrapper<>(null, Set.of(Grant.VIEW_CHILDREN))));

        assertThat(underTest.isMasked_labelAlm(SHARED_WITH, event, null, LABEL_ID)).isFalse();
    }

    @Test
    void isMasked_labelAlm_nullAlm_labelAvailable_hasNoViewGrant() {
        given(labelObjectQueryService.findLabel(SHARED_WITH, LABEL_ID)).willReturn(Optional.of(new BiWrapper<>(null, Set.of(Grant.SEE_CHILDREN))));

        assertThat(underTest.isMasked_labelAlm(SHARED_WITH, event, null, LABEL_ID)).isTrue();
    }

    @Test
    void isMasked_labelAlm_nullAlm_labelUnavailable() {
        given(labelObjectQueryService.findLabel(SHARED_WITH, LABEL_ID)).willReturn(Optional.empty());

        assertThat(catchThrowable(() -> underTest.isMasked_labelAlm(SHARED_WITH, event, null, LABEL_ID))).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void isMasked_labelAlm_hasGrant() {
        given(event.getUserId()).willReturn(USER_ID);
        given(alm1.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));

        assertThat(underTest.isMasked_labelAlm(SHARED_WITH, event, alm1, LABEL_ID)).isFalse();
    }

    @Test
    void isMasked_labelAlm_eventAlmHasGrant() {
        given(event.getUserId()).willReturn(USER_ID);
        given(alm1.getGrants()).willReturn(Set.of());
        given(event.getEventId()).willReturn(EVENT_ID);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm2));
        given(alm2.getGrants()).willReturn(Set.of(Grant.VIEW));

        assertThat(underTest.isMasked_labelAlm(SHARED_WITH, event, alm1, LABEL_ID)).isFalse();
    }

    @Test
    void isMasked_labelAlm_masked() {
        given(event.getUserId()).willReturn(USER_ID);
        given(alm1.getGrants()).willReturn(Set.of());
        given(event.getEventId()).willReturn(EVENT_ID);
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(alm2));
        given(alm2.getGrants()).willReturn(Set.of(Grant.SEE));

        assertThat(underTest.isMasked_labelAlm(SHARED_WITH, event, alm1, LABEL_ID)).isTrue();
    }

    @Test
    void isMasked_eventAlm_containsGrant() {
        given(alm1.getGrants()).willReturn(Set.of(Grant.VIEW));

        assertThat(underTest.isMasked_eventAlm(SHARED_WITH, event, alm1)).isFalse();
    }

    @Test
    void isMasked_eventAlm_sharedLabelContainsGrant() {
        given(alm1.getGrants()).willReturn(Set.of(Grant.SEE));
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of(alm2));
        given(alm2.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));
        given(alm2.getOwner()).willReturn(USER_ID);
        given(alm2.getObjectId()).willReturn(LABEL_ID);
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));
        given(labelEventMapping.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(event.getEventId()).willReturn(EVENT_ID);

        assertThat(underTest.isMasked_eventAlm(SHARED_WITH, event, alm1)).isFalse();
    }

    @Test
    void isMasked_eventAlm_hasNoGrant() {
        given(alm1.getGrants()).willReturn(Set.of(Grant.SEE));
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of(alm2));
        given(alm2.getGrants()).willReturn(Set.of(Grant.SEE_CHILDREN));

        assertThat(underTest.isMasked_eventAlm(SHARED_WITH, event, alm1)).isTrue();
    }
}