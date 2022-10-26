package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.model.EventSearchResponse;
import com.github.saphyra.apphub.api.diary.server.CalendarController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.OneParamRequest;
import com.github.saphyra.apphub.service.diary.service.calendar.search.CalendarSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
class CalendarControllerImpl implements CalendarController {
    private final CalendarQueryService calendarQueryService;
    private final CalendarSearchService calendarSearchService;

    @Override
    public List<CalendarResponse> getCalendar(String date, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his calendar for date {}.", accessTokenHeader.getUserId(), date);
        return calendarQueryService.getCalendarForMonth(accessTokenHeader.getUserId(), LocalDate.parse(date));
    }

    @Override
    public List<EventSearchResponse> search(OneParamRequest<String> query, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to search in the calendar.", accessTokenHeader.getUserId());
        return calendarSearchService.search(accessTokenHeader.getUserId(), query.getValue());
    }
}
