package com.github.saphyra.apphub.service.diary.service.occurrence.service.query;

import com.github.saphyra.apphub.service.diary.dao.occurance.Occurrence;
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
public class OneTimeEventHandlerTest {
    private static final LocalDate DATE = LocalDate.now();

    @InjectMocks
    private OneTimeEventHandler underTest;

    @Mock
    private Occurrence occurrence;

    @Test
    public void handleOneTimeEvent_shouldReturn() {
        given(occurrence.getDate()).willReturn(DATE);

        List<Occurrence> result = underTest.handleOneTimeEvent(List.of(occurrence), List.of(DATE));

        assertThat(result).containsExactly(occurrence);
    }

    @Test
    public void handleOneTimeEvent_shouldNotReturn() {
        given(occurrence.getDate()).willReturn(DATE.plusDays(1));

        List<Occurrence> result = underTest.handleOneTimeEvent(List.of(occurrence), List.of(DATE));

        assertThat(result).isEmpty();
    }
}