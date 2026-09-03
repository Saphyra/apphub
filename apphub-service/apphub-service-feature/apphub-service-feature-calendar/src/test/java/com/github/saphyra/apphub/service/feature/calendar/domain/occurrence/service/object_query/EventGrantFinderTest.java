package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventFactory;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.label.dao.Label;
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
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class EventGrantFinderTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID LABEL_ID_1 = UUID.randomUUID();
    private static final UUID LABEL_ID_2 = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @Mock
    private AlmDao almDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private EventFactory eventFactory;

    @Mock
    private LabelObjectQueryService labelObjectQueryService;

    @InjectMocks
    private EventGrantFinder underTest;

    @Mock
    private Event event;

    @Mock
    private Alm eventAlm;

    @Mock
    private Alm labelAlm1;

    @Mock
    private Alm labelAlm2;

    @Mock
    private LabelEventMapping labelEventMapping1;

    @Mock
    private LabelEventMapping labelEventMapping2;

    @Mock
    private Label label;

    @Test
    void ownEvent() {
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));

        assertThat(underTest.getEventWithGrants(USER_ID, EVENT_ID)).isEqualTo(new BiWrapper<>(event, Grant.forType(SharedObjectType.EVENT)));
    }

    @Test
    void eventNotFound() {
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(USER_ID, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.LABEL)).willReturn(List.of());
        given(eventFactory.dummyEvent(USER_ID, EVENT_ID)).willReturn(event);
        given(labelObjectQueryService.getByUserId(USER_ID)).willReturn(Stream.of());

        assertThat(underTest.getEventWithGrants(USER_ID, EVENT_ID)).isEqualTo(new BiWrapper<>(event, Set.of()));
    }

    @Test
    void sharedEvent() {
        given(eventDao.findById(SHARED_WITH, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(eventAlm));
        given(eventAlm.getOwner()).willReturn(USER_ID);
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));
        given(eventAlm.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of());
        given(labelObjectQueryService.getByUserId(SHARED_WITH)).willReturn(Stream.of());

        assertThat(underTest.getEventWithGrants(SHARED_WITH, EVENT_ID)).isEqualTo(new BiWrapper<>(event, Set.of(Grant.VIEW_CHILDREN)));
    }

    @Test
    void sharedLabel() {
        given(eventDao.findById(SHARED_WITH, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of(labelAlm1));
        given(labelAlm1.getOwner()).willReturn(USER_ID);
        given(labelAlm1.getObjectId()).willReturn(LABEL_ID_1);
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID_1)).willReturn(Optional.of(labelEventMapping1));
        given(labelEventMapping1.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(labelAlm1.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));
        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);
        given(labelObjectQueryService.getByUserId(SHARED_WITH)).willReturn(Stream.of());

        assertThat(underTest.getEventWithGrants(SHARED_WITH, EVENT_ID)).isEqualTo(new BiWrapper<>(event, Set.of(Grant.VIEW, Grant.VIEW_CHILDREN)));
    }

    @Test
    void childEventOfVisibleLabel() {
        given(eventDao.findById(SHARED_WITH, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.empty());
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of());

        given(labelObjectQueryService.getByUserId(SHARED_WITH)).willReturn(Stream.of(new BiWrapper<>(label, Set.of(Grant.VIEW_CHILDREN))));
        given(label.getUserId()).willReturn(USER_ID);
        given(label.getLabelId()).willReturn(LABEL_ID_1);
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID_1)).willReturn(Optional.of(labelEventMapping1));
        given(labelEventMapping1.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);

        assertThat(underTest.getEventWithGrants(SHARED_WITH, EVENT_ID)).isEqualTo(new BiWrapper<>(event, Set.of(Grant.VIEW, Grant.VIEW_CHILDREN)));
    }

    @Test
    void aggregateGrants() {
        given(eventDao.findById(SHARED_WITH, EVENT_ID)).willReturn(Optional.empty());
        given(almDao.findForObject(SHARED_WITH, PrincipalType.USER, EVENT_ID, SharedObjectType.EVENT)).willReturn(Optional.of(eventAlm));
        given(eventAlm.getOwner()).willReturn(USER_ID);
        given(eventDao.findById(USER_ID, EVENT_ID)).willReturn(Optional.of(event));
        given(eventAlm.getGrants()).willReturn(Set.of(Grant.DELETE));

        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.LABEL)).willReturn(List.of(labelAlm1, labelAlm2));
        given(labelAlm1.getOwner()).willReturn(USER_ID);
        given(labelAlm1.getObjectId()).willReturn(LABEL_ID_1);
        given(labelAlm2.getOwner()).willReturn(USER_ID);
        given(labelAlm2.getObjectId()).willReturn(LABEL_ID_2);

        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID_1)).willReturn(Optional.of(labelEventMapping1));
        given(eventLabelMappingDao.getEventsOfLabel(USER_ID, LABEL_ID_2)).willReturn(Optional.of(labelEventMapping2));
        given(labelEventMapping1.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(labelEventMapping2.getEventIds()).willReturn(Map.of(EVENT_ID, USER_ID));
        given(labelAlm1.getGrants()).willReturn(Set.of(Grant.VIEW_CHILDREN));
        given(labelAlm2.getGrants()).willReturn(Set.of(Grant.SEE_CHILDREN));

        given(labelObjectQueryService.getByUserId(SHARED_WITH)).willReturn(Stream.of(new BiWrapper<>(label, Set.of(Grant.SHARE_CHILDREN))));
        given(label.getUserId()).willReturn(USER_ID);
        given(label.getLabelId()).willReturn(LABEL_ID_1);

        given(eventDao.findByIdValidated(USER_ID, EVENT_ID)).willReturn(event);

        BiWrapper<Event, Set<Grant>> result = underTest.getEventWithGrants(SHARED_WITH, EVENT_ID);

        assertThat(result.getEntity1()).isEqualTo(event);
        assertThat(result.getEntity2()).containsExactlyInAnyOrder(Grant.VIEW, Grant.VIEW_CHILDREN, Grant.SEE, Grant.SEE_CHILDREN, Grant.SHARE, Grant.SHARE_CHILDREN, Grant.DELETE);
    }
}