package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.api.feature.calendar.model.SharedObjectType;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.Alm;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.AlmDao;
import com.github.saphyra.apphub.service.feature.calendar.domain.share.dao.PrincipalType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class GetOccurrencesOfUserServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private EventGrantFinder eventGrantFinder;

    @Mock
    private AlmDao almDao;

    @Mock
    private GetOccurrencesOfUserServiceHelper helper;

    @InjectMocks
    private GetOccurrencesOfUserService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Alm alm;

    @BeforeEach
    void setUp() {
        given(helper.getOccurrences(USER_ID)).willReturn(List.of(occurrence));
        given(occurrence.getEventId()).willReturn(EVENT_ID);
        given(event.getEventId()).willReturn(EVENT_ID);
    }

    @Test
    void ownEvent() {
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Grant.forType(SharedObjectType.EVENT)));

        assertThat(underTest.getOccurrences(USER_ID)).containsEntry(event, List.of(occurrence));
    }

    @Test
    void ownOccurrence() {
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.VIEW)));
        given(occurrence.getUserId()).willReturn(USER_ID);

        assertThat(underTest.getOccurrences(USER_ID)).containsEntry(event, List.of(occurrence));
    }

    @Test
    void sharedOccurrence_unmasked() {
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.SEE)));
        given(occurrence.getUserId()).willReturn(UUID.randomUUID());
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(almDao.findForObject(USER_ID, PrincipalType.USER, OCCURRENCE_ID, SharedObjectType.OCCURRENCE)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.VIEW));

        assertThat(underTest.getOccurrences(USER_ID)).containsEntry(event, List.of(occurrence));

        then(occurrence).should(never()).setMasked(anyBoolean());
    }

    @Test
    void sharedOccurrence_masked() {
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.SEE)));
        given(occurrence.getUserId()).willReturn(UUID.randomUUID());
        given(occurrence.getOccurrenceId()).willReturn(OCCURRENCE_ID);
        given(almDao.findForObject(USER_ID, PrincipalType.USER, OCCURRENCE_ID, SharedObjectType.OCCURRENCE)).willReturn(Optional.of(alm));
        given(alm.getGrants()).willReturn(Set.of(Grant.SEE));

        assertThat(underTest.getOccurrences(USER_ID)).containsEntry(event, List.of(occurrence));

        then(occurrence).should().setMasked(true);
    }
}