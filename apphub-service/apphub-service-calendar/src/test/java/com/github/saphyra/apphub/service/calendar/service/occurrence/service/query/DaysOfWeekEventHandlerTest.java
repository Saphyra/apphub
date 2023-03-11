package com.github.saphyra.apphub.service.calendar.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.calendar.dao.event.Event;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.calendar.dao.occurance.OccurrenceType;
import com.github.saphyra.apphub.service.calendar.service.occurrence.service.OccurrenceFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DaysOfWeekEventHandlerTest {
    private final static LocalDate START_DATE = LocalDate.parse("2022-07-16");
    private final static LocalDate MONDAY_1 = LocalDate.parse("2022-07-04");
    private final static LocalDate MONDAY_2 = LocalDate.parse("2022-07-11");
    private final static LocalDate MONDAY_3 = LocalDate.parse("2022-07-18");
    private final static LocalDate OTHER_DAY = LocalDate.parse("2022-07-12");

    @Mock
    private DaysOfWeekParser daysOfWeekParser;

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private RepeatedEventPostCollector repeatedEventPostCollector;

    @InjectMocks
    private DaysOfWeekEventHandler underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence startOccurrence;

    @Mock
    private Occurrence existingOccurrence;

    @Mock
    private Occurrence newOccurrence;

    @Mock
    private Occurrence resultOccurrence;

    @Captor
    private ArgumentCaptor<List<Occurrence>> argumentCaptor;

    @Test
    public void handleDaysOfWeekEvent() {
        List<LocalDate> dates = List.of(START_DATE, MONDAY_1, MONDAY_2, MONDAY_3, OTHER_DAY);

        given(daysOfWeekParser.parseDaysOfWeek(event)).willReturn(List.of(DayOfWeek.MONDAY));
        given(event.getStartDate()).willReturn(MONDAY_2.minusDays(1));
        given(occurrenceFactory.createVirtual(MONDAY_3, event, OccurrenceType.DEFAULT)).willReturn(newOccurrence);
        given(repeatedEventPostCollector.collect(eq(event), anyList(), eq(dates))).willReturn(List.of(resultOccurrence));

        List<Occurrence> result = underTest.handleDaysOfWeekEvent(
            event,
            dates,
            CollectionUtils.toMap(
                new OptionalHashMap<>(),
                new BiWrapper<>(MONDAY_2, existingOccurrence),
                new BiWrapper<>(START_DATE, startOccurrence)
            )
        );

        assertThat(result).containsExactly(resultOccurrence);
        verify(repeatedEventPostCollector).collect(eq(event), argumentCaptor.capture(), eq(dates));
        assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(existingOccurrence, newOccurrence, startOccurrence);

        verify(occurrenceDao).save(newOccurrence);
    }
}