package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.ReferenceDate;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.query.OccurrenceQueryService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CalendarQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalDate DATE_OF_MONTH = DATE.plusDays(1);
    private static final LocalDate MONTH = DATE.minusDays(1);
    private static final LocalDate DAY = DATE.minusDays(2);

    @Mock
    private DaysOfMonthProvider daysOfMonthProvider;

    @Mock
    private OccurrenceQueryService occurrenceQueryService;

    @Mock
    private CalendarResponseFactory calendarResponseFactory;

    @InjectMocks
    private CalendarQueryService underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private CalendarResponse calendarResponse;

    @Captor
    private ArgumentCaptor<Collection<LocalDate>> argumentCaptor;

    @Test
    public void getCalendar() {
        ReferenceDate referenceDate = ReferenceDate.builder()
            .month(MONTH)
            .day(DAY)
            .build();

        given(daysOfMonthProvider.getDaysOfMonth(MONTH)).willReturn(CollectionUtils.toSet(DATE_OF_MONTH));
        given(occurrenceQueryService.getOccurrences(eq(USER_ID), any())).willReturn(Map.of(DATE_OF_MONTH, List.of(occurrence)));
        given(calendarResponseFactory.create(DATE_OF_MONTH, List.of(occurrence))).willReturn(calendarResponse);

        List<CalendarResponse> result = underTest.getCalendar(USER_ID, referenceDate);

        assertThat(result).containsExactly(calendarResponse);

        verify(occurrenceQueryService).getOccurrences(eq(USER_ID), argumentCaptor.capture());
        assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(DATE_OF_MONTH, DAY);
    }

    @Test
    public void getCalendarForMonth() {
        given(daysOfMonthProvider.getDaysOfMonth(DATE)).willReturn(Set.of(DATE_OF_MONTH));
        given(occurrenceQueryService.getOccurrences(USER_ID, Set.of(DATE_OF_MONTH))).willReturn(Map.of(DATE_OF_MONTH, List.of(occurrence)));
        given(calendarResponseFactory.create(DATE_OF_MONTH, List.of(occurrence))).willReturn(calendarResponse);

        List<CalendarResponse> result = underTest.getCalendarForMonth(USER_ID, DATE);

        assertThat(result).containsExactly(calendarResponse);
    }
}