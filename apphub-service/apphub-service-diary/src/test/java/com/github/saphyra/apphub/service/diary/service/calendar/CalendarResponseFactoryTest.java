package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.OccurrenceResponse;
import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.diary.service.occurrence.service.OccurrenceToResponseConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CalendarResponseFactoryTest {
    private static final LocalDate DATE = LocalDate.now();
    @Mock
    private OccurrenceToResponseConverter occurrenceToResponseConverter;

    @InjectMocks
    private CalendarResponseFactory underTest;

    @Mock
    private Occurrence occurrence;

    @Mock
    private OccurrenceResponse occurrenceResponse;

    @Test
    public void create() {
        given(occurrenceToResponseConverter.convert(List.of(occurrence))).willReturn(List.of(occurrenceResponse));

        CalendarResponse result = underTest.create(DATE, List.of(occurrence));

        assertThat(result.getDate()).isEqualTo(DATE);
        assertThat(result.getEvents()).containsExactly(occurrenceResponse);
    }
}