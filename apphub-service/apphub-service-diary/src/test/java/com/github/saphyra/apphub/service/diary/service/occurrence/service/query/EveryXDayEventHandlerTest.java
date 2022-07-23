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

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class EveryXDayEventHandlerTest {
    private static final LocalDate DATE_1 = LocalDate.parse("2022-07-01");
    private static final LocalDate DATE_2 = LocalDate.parse("2022-07-02");
    private static final LocalDate DATE_3 = LocalDate.parse("2022-07-03");
    private static final LocalDate DATE_4 = LocalDate.parse("2022-07-04");
    private static final LocalDate DATE_5 = LocalDate.parse("2022-07-05");
    private static final LocalDate DATE_6 = LocalDate.parse("2022-07-06");
    private static final LocalDate DATE_7 = LocalDate.parse("2022-07-07");
    private static final List<LocalDate> DATES = List.of(
        DATE_1,
        DATE_2,
        DATE_3,
        DATE_4,
        DATE_5,
        DATE_6,
        DATE_7
    );

    @Mock
    private OccurrenceFactory occurrenceFactory;

    @Mock
    private DateOfLastOccurrenceProvider dateOfLastOccurrenceProvider;

    @Mock
    private OccurrenceDao occurrenceDao;

    @InjectMocks
    private EveryXDayEventHandler underTest;

    @Mock
    private Event event;

    @Mock
    private Occurrence existingOccurrence;

    @Mock
    private Occurrence newOccurrence;

    @Test
    public void handleEveryXDayEvent() {
        OptionalHashMap<LocalDate, Occurrence> occurrenceMapping = CollectionUtils.singleValueMap(DATE_5, existingOccurrence, new OptionalHashMap<>());

        given(event.getStartDate()).willReturn(DATE_4);
        given(event.getRepetitionData()).willReturn(String.valueOf(2));
        given(dateOfLastOccurrenceProvider.getDateOfLastOccurrence(eq(occurrenceMapping.values()), eq(event), any())).willReturn(DATE_1);
        given(occurrenceFactory.createVirtual(DATE_7, event)).willReturn(newOccurrence);

        List<Occurrence> result = underTest.handleEveryXDayEvent(event, DATES, occurrenceMapping);

        assertThat(result).containsExactlyInAnyOrder(existingOccurrence, newOccurrence);

        verify(occurrenceDao).save(newOccurrence);
    }
}