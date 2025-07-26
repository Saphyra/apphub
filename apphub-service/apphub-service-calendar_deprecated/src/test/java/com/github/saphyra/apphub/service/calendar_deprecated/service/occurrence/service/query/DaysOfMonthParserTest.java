package com.github.saphyra.apphub.service.calendar_deprecated.service.occurrence.service.query;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.saphyra.apphub.lib.common_util.ObjectMapperWrapper;
import com.github.saphyra.apphub.service.calendar_deprecated.dao.event.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class DaysOfMonthParserTest {
    @Spy
    private final ObjectMapperWrapper objectMapperWrapper = new ObjectMapperWrapper(new ObjectMapper());

    @InjectMocks
    private DaysOfMonthParser underTest;

    @Mock
    private Event event;

    @Test
    public void parseDaysOfMonth() {
        List<Integer> daysOfMonth = List.of(5, 23);
        String daysOfMonthString = objectMapperWrapper.writeValueAsString(daysOfMonth);
        given(event.getRepetitionData()).willReturn(daysOfMonthString);

        List<Integer> result = underTest.parseDaysOfMonth(event);

        assertThat(result).isEqualTo(daysOfMonth);
    }
}