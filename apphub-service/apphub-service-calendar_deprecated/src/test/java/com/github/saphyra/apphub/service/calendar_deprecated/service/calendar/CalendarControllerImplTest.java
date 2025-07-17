package com.github.saphyra.apphub.service.calendar_deprecated.service.calendar;

import com.github.saphyra.apphub.api.calendar.model.CalendarResponse;
import com.github.saphyra.apphub.api.calendar.model.EventSearchResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.calendar_deprecated.service.calendar.search.CalendarSearchService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class CalendarControllerImplTest {
    private static final UUID USER_ID = UUID.randomUUID();
    private static final LocalDate DATE = LocalDate.now();
    private static final String QUERY = "query";

    @Mock
    private CalendarQueryService calendarQueryService;

    @Mock
    private CalendarSearchService calendarSearchService;

    @InjectMocks
    private CalendarControllerImpl underTest;

    @Mock
    private AccessTokenHeader accessTokenHeader;

    @Mock
    private CalendarResponse calendarResponse;

    @Mock
    private EventSearchResponse eventSearchResponse;

    @BeforeEach
    public void setUp() {
        given(accessTokenHeader.getUserId()).willReturn(USER_ID);
    }

    @Test
    public void getCalendar() {
        given(calendarQueryService.getCalendarForMonth(USER_ID, DATE)).willReturn(List.of(calendarResponse));

        List<CalendarResponse> result = underTest.getCalendar(DATE.toString(), accessTokenHeader);

        assertThat(result).containsExactly(calendarResponse);
    }

    @Test
    public void search() {
        given(calendarSearchService.search(USER_ID, QUERY)).willReturn(List.of(eventSearchResponse));

        List<EventSearchResponse> result = underTest.search(new OneParamRequest<>(QUERY), accessTokenHeader);

        assertThat(result).containsExactly(eventSearchResponse);
    }
}