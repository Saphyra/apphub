package com.github.saphyra.apphub.service.diary.service.calendar;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.api.diary.server.CalendarController;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
//TODO unit test
class CalendarControllerImpl implements CalendarController {
    private final CalendarQueryService calendarQueryService;

    @Override
    public List<CalendarResponse> getCalendar(String date, AccessTokenHeader accessTokenHeader) {
        log.info("{} wants to know his calendar for date {}.", accessTokenHeader.getUserId(), date);
        return calendarQueryService.getCalendar(accessTokenHeader.getUserId(), LocalDate.parse(date));
    }
}
