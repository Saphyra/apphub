package com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.search;

import com.github.saphyra.apphub.api.calendar.model.EventSearchResponse;
import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.EventDao;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.test.common.ExceptionValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CalendarSearchServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final String QUERY = "query";
    private static final UUID MATCHING_EVENT_ID = UUID.randomUUID();
    private static final UUID MATCHING_OCCURRENCE_EVENT_ID = UUID.randomUUID();
    private static final UUID ANOTHER_EVENT_ID = UUID.randomUUID();

    @Mock
    private EventDao eventDao;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private CalendarSearchResultMapper calendarSearchResultMapper;

    @InjectMocks
    private CalendarSearchService underTest;

    @Mock
    private Event matchingEvent;

    @Mock
    private Event anotherEvent;

    @Mock
    private Occurrence matchingOccurrence;

    @Mock
    private Occurrence anotherOccurrence;

    @Mock
    private EventSearchResponse eventSearchResponse;

    @Test
    public void queryTooShort() {
        Throwable ex = catchThrowable(() -> underTest.search(USER_ID, "as"));

        ExceptionValidator.validateInvalidParam(ex, "value", "too short");
    }

    @Test
    public void search() {
        given(eventDao.getByUserId(USER_ID)).willReturn(List.of(matchingEvent, anotherEvent));
        given(occurrenceDao.getByUserId(USER_ID)).willReturn(List.of(matchingOccurrence, anotherOccurrence));

        given(matchingEvent.toString()).willReturn("wrgqUERysf");
        given(matchingEvent.getEventId()).willReturn(MATCHING_EVENT_ID);
        given(anotherEvent.toString()).willReturn("asd");
        given(anotherEvent.getEventId()).willReturn(ANOTHER_EVENT_ID);

        given(matchingOccurrence.toString()).willReturn("wrgqUERysf");
        given(matchingOccurrence.getEventId()).willReturn(MATCHING_OCCURRENCE_EVENT_ID);

        given(anotherOccurrence.toString()).willReturn("asd");

        given(calendarSearchResultMapper.map(
            Set.of(
                MATCHING_EVENT_ID,
                MATCHING_OCCURRENCE_EVENT_ID
            ),
            CollectionUtils.toMap(
                new BiWrapper<>(MATCHING_EVENT_ID, matchingEvent),
                new BiWrapper<>(ANOTHER_EVENT_ID, anotherEvent)
            ),
            List.of(
                matchingOccurrence,
                anotherOccurrence
            )
        )).willReturn(List.of(eventSearchResponse));

        List<EventSearchResponse> result = underTest.search(USER_ID, QUERY);

        assertThat(result).containsExactly(eventSearchResponse);
    }
}