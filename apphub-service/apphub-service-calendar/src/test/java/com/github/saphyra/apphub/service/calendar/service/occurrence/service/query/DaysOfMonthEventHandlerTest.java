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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class DaysOfMonthEventHandlerTest {
    private final static LocalDate START_DATE = LocalDate.parse("2022-08-07");
    private final static LocalDate MONTH_DAY_1 = LocalDate.parse("2022-07-01");
    private final static LocalDate MONTH_DAY_2 = LocalDate.parse("2022-08-01");
    private final static LocalDate MONTH_DAY_3 = LocalDate.parse("2022-09-01");
    private final static LocalDate OTHER_DAY = LocalDate.parse("2022-07-12");

    @Mock
    private DaysOfMonthParser daysOfMonthParser;

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private OccurrenceDao occurrenceDao;

    @Mock
    private RepeatedEventPostCollector repeatedEventPostCollector;

    @InjectMocks
    private DaysOfMonthEventHandler underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence existingOccurrence;

    @Mock
    private Occurrence newOccurrence;

    @Mock
    private Occurrence startOccurrence;

    @Mock
    private Occurrence resultOccurrence;

    @Captor
    private ArgumentCaptor<List<Occurrence>> argumentCaptor;

    @Test
    public void handleDaysOfMonthEvent() {
        List<LocalDate> dates = List.of(START_DATE, MONTH_DAY_1, MONTH_DAY_2, MONTH_DAY_3, OTHER_DAY);

        given(daysOfMonthParser.parseDaysOfMonth(event)).willReturn(List.of(1));
        given(event.getStartDate()).willReturn(MONTH_DAY_2);
        given(occurrenceFactory.createVirtual(MONTH_DAY_3, event, OccurrenceType.DEFAULT)).willReturn(newOccurrence);
        given(repeatedEventPostCollector.collect(eq(event), any(), eq(dates))).willReturn(List.of(resultOccurrence));


        List<Occurrence> result = underTest.handleDaysOfMonthEvent(
            event,
            dates,
            CollectionUtils.toMap(
                new OptionalHashMap<>(),
                new BiWrapper<>(MONTH_DAY_2, existingOccurrence),
                new BiWrapper<>(START_DATE, startOccurrence)
            )
        );

        assertThat(result).containsExactly(resultOccurrence);

        verify(occurrenceDao).save(newOccurrence);
        verify(repeatedEventPostCollector).collect(eq(event), argumentCaptor.capture(), eq(dates));
        assertThat(argumentCaptor.getValue()).containsExactlyInAnyOrder(existingOccurrence, newOccurrence, startOccurrence);
    }
}