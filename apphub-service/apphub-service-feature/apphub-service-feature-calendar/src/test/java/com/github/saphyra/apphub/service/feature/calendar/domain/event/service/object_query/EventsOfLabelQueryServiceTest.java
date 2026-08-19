package com.github.saphyra.apphub.service.feature.calendar.domain.event.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
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
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventsOfLabelQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private AlmDao almDao;

    @Mock
    private EventDao eventDao;

    @Mock
    private EventMaskedChecker eventMaskedChecker;

    @InjectMocks
    private EventsOfLabelQueryService underTest;

    @Mock
    private LabelEventMapping labelEventMapping;

    @Mock
    private Event event;

    @Mock
    private Alm alm;

    @Test
    void ownLabel() {
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));
        given(labelEventMapping.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(eventDao.getByIds(List.of(new BiWrapper<>(USER_ID, EVENT_ID)))).willReturn(List.of(event));
        given(eventMaskedChecker.isMasked_labelAlm(USER_ID, event, null)).willReturn(false);
        given(event.setMasked(false)).willReturn(event);

        assertThat(underTest.getEventsOfLabel(USER_ID, LABEL_ID)).containsExactly(event);
    }

    @Test
    void sharedLabel_noGrants() {
        given(eventLabelMappingDao.getEventsOfLabel(SHARED_WITH, LABEL_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of());

        assertThat(underTest.getEventsOfLabel(SHARED_WITH, LABEL_ID)).isEmpty();
    }

    @Test
    void sharedLabel_withGrants() {
        given(eventLabelMappingDao.getEventsOfLabel(SHARED_WITH, LABEL_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, LABEL_ID, SharedObjectType.LABEL)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));
        given(alm.getOwner()).willReturn(USER_ID);
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID)).willReturn(Optional.of(labelEventMapping));
        given(eventMaskedChecker.isMasked_labelAlm(SHARED_WITH, event, alm)).willReturn(false);
        given(event.setMasked(false)).willReturn(event);
        given(labelEventMapping.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(eventDao.getByIds(List.of(new BiWrapper<>(USER_ID, EVENT_ID)))).willReturn(List.of(event));

        assertThat(underTest.getEventsOfLabel(SHARED_WITH, LABEL_ID)).containsExactly(event);
    }
}