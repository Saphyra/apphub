package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.domain.event.dao.Event;
import com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.dao.Occurrence;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FindOccurrenceServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private EventGrantFinder eventGrantFinder;

    @Mock
    private OccurrenceGrantFinder occurrenceGrantFinder;

    @InjectMocks
    private FindOccurrenceService underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private Event event;

    @Test
    void findOccurrence_masked() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of()));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.SEE, Grant.SEE_CHILDREN)));
        given(event.setMasked(true)).willReturn(event);
        given(occurrence.setMasked(true)).willReturn(occurrence);

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void findOccurrence_onlyOccurrenceMasked() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of(Grant.SEE)));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.VIEW)));
        given(event.setMasked(false)).willReturn(event);
        given(occurrence.setMasked(true)).willReturn(occurrence);

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void findOccurrence_onlyEventMasked() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of(Grant.VIEW)));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.SEE)));
        given(event.setMasked(true)).willReturn(event);
        given(occurrence.setMasked(false)).willReturn(occurrence);

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void findOccurrence_unmasked() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of(Grant.VIEW)));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.VIEW)));
        given(event.setMasked(false)).willReturn(event);
        given(occurrence.setMasked(false)).willReturn(occurrence);

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void findOccurrence_projectedGrant() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of()));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.VIEW, Grant.SEE_CHILDREN)));
        given(event.setMasked(false)).willReturn(event);
        given(occurrence.setMasked(true)).willReturn(occurrence);

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void forbiddenOperation() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of()));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of()));

        ExceptionValidator.validateForbiddenOperation(() -> underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID));
    }
}