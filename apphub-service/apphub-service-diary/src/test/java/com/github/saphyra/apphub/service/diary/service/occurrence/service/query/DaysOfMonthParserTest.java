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

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
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