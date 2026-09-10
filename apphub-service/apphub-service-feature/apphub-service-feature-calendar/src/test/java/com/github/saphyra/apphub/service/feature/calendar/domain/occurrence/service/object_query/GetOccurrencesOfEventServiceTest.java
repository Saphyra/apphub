package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.OccurrenceDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.lenient;

@ExtendWith(MockitoExtension.class)
class GetOccurrencesOfEventServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID SHARED_WITH = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private EventGrantFinder eventGrantFinder;

    @Mock
    private AlmDao almDao;

    @InjectMocks
    private GetOccurrencesOfEventService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Alm alm;

    @Test
    void ownOccurrence() {
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(almDao.getByUserIdAndObjectType(USER_ID, SharedObjectType.OCCURRENCE)).willReturn(List.of());
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Grant.forType(SharedObjectType.EVENT)));
        given(event.setMasked(false)).willReturn(event);
        given(occurrence.getUserId()).willReturn(USER_ID);

        assertThat(underTest.getOccurrences(USER_ID, EVENT_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(List.of(occurrence), BiWrapper::getEntity2);
    }

    @ParameterizedTest
    @MethodSource("sharedOccurrenceProvider")
    void sharedOccurrence(Set<Grant> eventGrants, boolean eventMasked, Set<Grant> occurrenceGrants, boolean occurrenceMasked) {
        given(occurrenceDao.getByEventId(EVENT_ID)).willReturn(List.of(occurrence));
        given(almDao.getByUserIdAndObjectType(SHARED_WITH, SharedObjectType.OCCURRENCE)).willReturn(List.of(alm));
        given(eventGrantFinder.getEventWithGrants(SHARED_WITH, EVENT_ID)).willReturn(new BiWrapper<>(event, eventGrants));
        given(event.setMasked(eventMasked)).willReturn(event);
        given(occurrence.getUserId()).willReturn(USER_ID);
        given(alm.getObjectId()).willReturn(OCCURRENCE_ID);
        given(alm.getGrants()).willReturn(occurrenceGrants);
        lenient().when(occurrence.getOccurrenceId()).thenReturn(OCCURRENCE_ID);
        if (occurrenceMasked) {
            given(occurrence.setMasked(true)).willReturn(occurrence);
        }

        assertThat(underTest.getOccurrences(SHARED_WITH, EVENT_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(List.of(occurrence), BiWrapper::getEntity2);
    }

    private static Stream<Arguments> sharedOccurrenceProvider() {
        return Stream.of(
            Arguments.of(Set.of(Grant.SEE), true, Set.of(Grant.SEE), true),
            Arguments.of(Set.of(Grant.VIEW), false, Set.of(Grant.VIEW), false),
            Arguments.of(Set.of(Grant.VIEW, Grant.SEE_CHILDREN), false, Set.of(), true),
            Arguments.of(Set.of(Grant.VIEW, Grant.VIEW_CHILDREN), false, Set.of(), false)
        );
    }
}