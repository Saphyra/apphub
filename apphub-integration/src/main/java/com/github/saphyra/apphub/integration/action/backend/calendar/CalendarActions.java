package com.github.saphyra.apphub.integration.action.backend.calendar;

import com.github.saphyra.apphub.integration.framework.CollectionUtils;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import io.restassured.response.Response;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarActions {
    public static List<CalendarResponse> getCalendar(int serverPort, UUID accessTokenId, LocalDate date) {
        Response response = getCalendarResponse(serverPort, accessTokenId, date);

        assertThat(response.getStatusCode()).isEqualTo(200);

        return CollectionUtils.toList(response.getBody().as(CalendarResponse[].class));
    }

    public static Response getCalendarResponse(int serverPort, UUID accessTokenId, LocalDate date) {
        return RequestFactory.createAuthorizedRequest(accessTokenId)
            .get(UrlFactory.create(serverPort, CalendarEndpoints.CALENDAR_GET_CALENDAR, Collections.emptyMap(), CollectionUtils.singleValueMap("date", date)));
    }
}
