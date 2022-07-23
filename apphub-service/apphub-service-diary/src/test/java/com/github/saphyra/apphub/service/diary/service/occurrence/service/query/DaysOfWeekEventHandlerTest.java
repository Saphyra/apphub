package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

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

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class DaysOfWeekEventHandlerTest {
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

    @InjectMocks
    private DaysOfWeekEventHandler underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence existingOccurrence;

    @Mock
    private Occurrence newOccurrence;

    @Test
    public void handleDaysOfWeekEvent() {
        given(daysOfWeekParser.parseDaysOfWeek(event)).willReturn(List.of(DayOfWeek.MONDAY));
        given(event.getStartDate()).willReturn(MONDAY_2.minusDays(1));
        given(occurrenceFactory.createVirtual(MONDAY_3, event)).willReturn(newOccurrence);

        List<Occurrence> result = underTest.handleDaysOfWeekEvent(event, List.of(MONDAY_1, MONDAY_2, MONDAY_3, OTHER_DAY), CollectionUtils.singleValueMap(MONDAY_2, existingOccurrence, new OptionalHashMap<>()));

        assertThat(result).containsExactlyInAnyOrder(existingOccurrence, newOccurrence);

        verify(occurrenceDao).save(newOccurrence);
    }
}