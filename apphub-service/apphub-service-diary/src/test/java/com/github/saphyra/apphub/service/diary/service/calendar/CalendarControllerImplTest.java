package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@RunWith(MockitoJUnitRunner.class)
public class CalendarControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();

    @Mock
    private CalendarQueryService calendarQueryService;

    @InjectMocks
    private CalendarControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CalendarResponse calendarResponse;

    @Before
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getCalendar() {
        given(calendarQueryService.getCalendar(USER_ID, DATE)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.getCalendar(DATE.toString(), accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }
}