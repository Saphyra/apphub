package com.github.saphyra.apphub.service.calendar.service.calendar.search;

import com.github.saphyra.apphub.api.calendar.model.EventSearchResponse;
import com.github.saphyra.apphub.api.calendar.model.OccurrenceSearchResponse;
import com.github.saphyra.apphub.api.calendar.model.RepetitionType;
import com.github.saphyra.apphub.lib.common_util.DateTimeUtil;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CalendarSearchResultMapperTest {
    private static final UUID EVENT_ID = UUID.randomUUID();
    private static final UUID OCCURRENCE_ID = UUID.randomUUID();
    private static final LocalTime CURRENT_TIME = LocalTime.now();
    private static final LocalDate EVENT_START_DATE = LocalDate.now();
    private static final String REPETITION_DATA = "repetition-data";
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final LocalDate OCCURRENCE_DATE = EVENT_START_DATE.plusDays(1);
    private static final LocalTime OCCURRENCE_TIME = CURRENT_TIME.plusSeconds(1);
    private static final String NOTE = "note";

    @Mock
    private DateTimeUtil dateTimeUtil;

    @InjectMocks
    private CalendarSearchResultMapper underTest;

    @Mock
    private Occurrence virtualOccurrence;

    @Mock
    private Occurrence anotherOccurrence;

    @Test
    public void map() {
        Event event = Event.builder()
            .eventId(EVENT_ID)
            .startDate(EVENT_START_DATE)
            .repetitionType(RepetitionType.DAYS_OF_WEEK)
            .repetitionData(REPETITION_DATA)
            .title(TITLE)
            .content(CONTENT)
            .build();

        Occurrence occurrence = Occurrence.builder()
            .occurrenceId(OCCURRENCE_ID)
            .eventId(EVENT_ID)
            .date(OCCURRENCE_DATE)
            .time(OCCURRENCE_TIME)
            .status(OccurrenceStatus.EXPIRED)
            .note(NOTE)
            .build();

        given(dateTimeUtil.getCurrentTime()).willReturn(CURRENT_TIME);
        given(virtualOccurrence.getStatus()).willReturn(OccurrenceStatus.VIRTUAL);
        given(virtualOccurrence.getEventId()).willReturn(EVENT_ID);
        given(anotherOccurrence.getEventId()).willReturn(UUID.randomUUID());

        List<EventSearchResponse> result = underTest.map(Set.of(EVENT_ID), CollectionUtils.toMap(EVENT_ID, event), List.of(occurrence, virtualOccurrence, anotherOccurrence));

        assertThat(result).hasSize(1);
        EventSearchResponse eventSearchResponse = result.get(0);
        assertThat(eventSearchResponse.getEventId()).isEqualTo(EVENT_ID);
        assertThat(eventSearchResponse.getTime()).isEqualTo(LocalDateTime.of(EVENT_START_DATE, CURRENT_TIME));
        assertThat(eventSearchResponse.getRepetitionType()).isEqualTo(RepetitionType.DAYS_OF_WEEK);
        assertThat(eventSearchResponse.getRepetitionData()).isEqualTo(REPETITION_DATA);
        assertThat(eventSearchResponse.getTitle()).isEqualTo(TITLE);
        assertThat(eventSearchResponse.getContent()).isEqualTo(CONTENT);
        assertThat(eventSearchResponse.getOccurrences()).hasSize(1);

        OccurrenceSearchResponse occurrenceSearchResponse = eventSearchResponse.getOccurrences()
            .get(0);
        assertThat(occurrenceSearchResponse.getOccurrenceId()).isEqualTo(OCCURRENCE_ID);
        assertThat(occurrenceSearchResponse.getDate()).isEqualTo(OCCURRENCE_DATE);
        assertThat(occurrenceSearchResponse.getTime()).isEqualTo(OCCURRENCE_TIME);
        assertThat(occurrenceSearchResponse.getStatus()).isEqualTo(OccurrenceStatus.EXPIRED.name());
        assertThat(occurrenceSearchResponse.getNote()).isEqualTo(NOTE);
    }
}