package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.lib.common_domain.BiWrapper;
import com.github.saphyra.apphub.lib.common_util.collection.CollectionUtils;
import com.github.saphyra.apphub.lib.common_util.collection.OptionalHashMap;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.dao.occurance.OccurrenceDao;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.OccurrenceFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
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

    @Test
    public void handleDaysOfMonthEvent() {
        given(daysOfMonthParser.parseDaysOfMonth(event)).willReturn(List.of(1));
        given(event.getStartDate()).willReturn(MONTH_DAY_2);
        given(occurrenceFactory.createVirtual(MONTH_DAY_3, event)).willReturn(newOccurrence);

        List<Occurrence> result = underTest.handleDaysOfMonthEvent(
            event,
            List.of(START_DATE, MONTH_DAY_1, MONTH_DAY_2, MONTH_DAY_3, OTHER_DAY),
            CollectionUtils.toMap(
                new OptionalHashMap<>(),
                new BiWrapper<>(MONTH_DAY_2, existingOccurrence),
                new BiWrapper<>(START_DATE, startOccurrence)
            )
        );

        assertThat(result).containsExactlyInAnyOrder(existingOccurrence, newOccurrence, startOccurrence);

        verify(occurrenceDao).save(newOccurrence);
    }
}