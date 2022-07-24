package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.diary.dao.event.Event;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.DayOfWeek;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class DaysOfWeekParserTest {
    private final static List<DayOfWeek> DAY_OF_WEEKS = List.of(DayOfWeek.MONDAY, DayOfWeek.SATURDAY);

    @Spy
    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    @InjectMocks
    private DaysOfWeekParser underTest;

    @Mock
    private Event event;

    @Test
    public void parseDaysOfWeek() {
        String repetitionData = objectMapperWrapper.writeValueAsString(DAY_OF_WEEKS);
        given(event.getRepetitionData()).willReturn(repetitionData);

        List<DayOfWeek> result = underTest.parseDaysOfWeek(event);

        assertThat(result).containsExactlyInAnyOrder(DayOfWeek.MONDAY, DayOfWeek.SATURDAY);
    }
}