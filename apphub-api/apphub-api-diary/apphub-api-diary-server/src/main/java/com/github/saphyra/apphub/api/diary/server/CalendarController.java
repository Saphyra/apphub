package com.github.saphyra.apphub.api.diary.server;

import com.github.saphyra.apphub.api.diary.model.CalendarResponse;
import com.github.saphyra.apphub.lib.common_domain.AccessTokenHeader;
import com.github.saphyra.apphub.lib.common_domain.Constants;
import com.github.saphyra.apphub.lib.config.Endpoints;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CalendarController {
    @GetMapping(Endpoints.DIARY_GET_CALENDAR)
    List<CalendarResponse> getCalendar(@RequestParam("date") String date, @RequestHeader(Constants.ACCESS_TOKEN_HEADER) AccessTokenHeader accessTokenHeader);
}
