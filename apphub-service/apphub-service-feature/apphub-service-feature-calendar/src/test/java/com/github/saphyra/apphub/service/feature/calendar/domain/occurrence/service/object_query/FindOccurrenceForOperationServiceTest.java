package com.github.saphyra.apphub.service.feature.calendar.domain.occurrence.service.object_query;

import com.github.saphyra.apphub.api.feature.calendar.model.Grant;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.service.feature.calendar.common.Operation;
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
class FindOccurrenceForOperationServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();

    @Mock
    private OccurrenceGrantFinder occurrenceGrantFinder;

    @Mock
    private EventGrantFinder eventGrantFinder;

    @InjectMocks
    private FindOccurrenceForOperationService underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence occurrence;

    @Test
    void findOccurrence() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of(Grant.EDIT)));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of(Grant.VIEW_CHILDREN)));

        assertThat(underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.EDIT))
            .returns(event, BiWrapper::getEntity1)
            .returns(occurrence, BiWrapper::getEntity2);
    }

    @Test
    void noGrant() {
        given(occurrenceGrantFinder.getOccurrenceWithGrants(USER_ID, EVENT_ID, OCCURRENCE_ID)).willReturn(new BiWrapper<>(occurrence, Set.of(Grant.EDIT)));
        given(eventGrantFinder.getEventWithGrants(USER_ID, EVENT_ID)).willReturn(new BiWrapper<>(event, Set.of()));

        ExceptionValidator.validateNotFoundException(() -> underTest.findOccurrence(USER_ID, EVENT_ID, OCCURRENCE_ID, Operation.EDIT));
    }
}