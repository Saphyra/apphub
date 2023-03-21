package com.github.saphyra.apphub.service.calendar.service.calendar;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.OccurrenceResponse;
import com.github.saphyra.apphub.service.calendar.dao.occurance.Occurrence;
import com.github.saphyra.apphub.service.calendar.service.occurrence.service.OccurrenceToResponseConverter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
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