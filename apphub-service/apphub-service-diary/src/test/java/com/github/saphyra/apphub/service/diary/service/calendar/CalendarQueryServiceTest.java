package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.MonthlyOccurrenceProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CalendarQueryServiceTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final LocalDate DATE_OF_MONTH = DATE.plusDays(1);

    @Mock
    private DaysOfMonthProvider daysOfMonthProvider;

    @Mock
    private MonthlyOccurrenceProvider monthlyOccurrenceProvider;

    @Mock
    private CalendarResponseFactory calendarResponseFactory;

    @InjectMocks
    private CalendarQueryService underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private CalendarResponse calendarResponse;

    @Test
    public void getCalendarForDay() {
        given(monthlyOccurrenceProvider.getOccurrencesOfDay(USER_ID, DATE)).willReturn(List.of(occurrence));
        given(calendarResponseFactory.create(DATE, List.of(occurrence))).willReturn(calendarResponse);

        CalendarResponse result = underTest.getCalendarForDay(USER_ID, DATE);

        assertThat(result).isEqualTo(calendarResponse);
    }

    @Test
    public void getCalendar() {
        given(daysOfMonthProvider.getDaysOfMonth(DATE)).willReturn(List.of(DATE_OF_MONTH));
        given(monthlyOccurrenceProvider.getOccurrencesOfMonth(USER_ID, List.of(DATE_OF_MONTH))).willReturn(Map.of(DATE_OF_MONTH, List.of(occurrence)));
        given(calendarResponseFactory.create(DATE_OF_MONTH, List.of(occurrence))).willReturn(calendarResponse);

        List<CalendarResponse> result = underTest.getCalendar(USER_ID, DATE);

        assertThat(result).containsExactly(calendarResponse);
    }
}