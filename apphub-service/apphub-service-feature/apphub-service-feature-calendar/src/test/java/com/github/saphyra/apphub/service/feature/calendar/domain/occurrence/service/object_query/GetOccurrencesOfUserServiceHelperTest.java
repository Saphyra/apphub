package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.EventDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.EventLabelMappingDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.event_label_mapping.dao.LabelEventMapping;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
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
class GetOccurrencesOfUserServiceHelperTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID OWNER = UUID.randomUUID();
    private static final UUID LABEL_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private AlmDao almDao;

    @Mock
    private EventLabelMappingDao eventLabelMappingDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EventDao eventDao;

    @InjectMocks
    private GetOccurrencesOfUserServiceHelper underTest;

    @Mock
    private Alm alm;

    @Mock
    private Occurrence occurrence;

    @Mock
    private LabelEventMapping labelEventMapping;

    @Mock
    private Event event;

    @ParameterizedTest
    @EnumSource(value = Grant.class, names = {"VIEW_CHILDREN", "SEE_CHILDREN"})
    void getSharedLabelOccurrences(Grant grant) {
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.LABEL)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of(grant));
        given(alm.getOwner()).willReturn(OWNER);
        given(alm.getObjectId()).willReturn(LABEL_ID);
        given(eventLabelMappingDao.getEventsOfLabel(OWNER, LABEL_ID)).willReturn(Optional.of(labelEventMapping));
        given(labelEventMapping.getEventIds()).willReturn(Map.of(EVENT_ID, OWNER));
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));

        assertThat(underTest.getSharedLabelOccurrences(USER_ID)).containsExactly(occurrence);
    }

    @Test
    void getSharedLabelOccurrences_noGrant() {
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.LABEL)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of());

        assertThat(underTest.getSharedLabelOccurrences(USER_ID)).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = Grant.class, names = {"VIEW_CHILDREN", "SEE_CHILDREN"})
    void getSharedEventOccurrences(Grant grant) {
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.EVENT)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of(grant));
        given(alm.getObjectId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));

        assertThat(underTest.getSharedEventOccurrences(USER_ID)).containsExactly(occurrence);
    }

    @Test
    void getSharedEventOccurrences_noGrant() {
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.EVENT)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of());

        assertThat(underTest.getSharedEventOccurrences(USER_ID)).isEmpty();
    }

    @ParameterizedTest
    @EnumSource(value = Grant.class, names = {"VIEW", "SEE"})
    void getSharedOccurrences(Grant grant) {
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of(grant));
        given(alm.getParent()).willReturn(EVENT_ID);
        given(alm.getObjectId()).willReturn(OCCURRENCE_ID);
        given(occurrenceDao.findByIdValidated(EVENT_ID, OCCURRENCE_ID)).willReturn(occurrence);

        assertThat(underTest.getSharedOccurrences(USER_ID)).containsExactly(occurrence);
    }

    @Test
    void getSharedOccurrences_noGrant() {
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));
        given(alm.getGrants()).willReturn(Set.of());

        assertThat(underTest.getSharedOccurrences(USER_ID)).isEmpty();
    }

    @Test
    void getOwnOccurrences() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(event));
        given(event.getEventId()).willReturn(EVENT_ID);
        given(occurrenceDao.getByEventId(event.getEventId())).willReturn(List.of(occurrence));

        assertThat(underTest.getOwnOccurrences(USER_ID)).containsExactly(occurrence);
    }
}