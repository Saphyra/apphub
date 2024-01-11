package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarActions;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.calendar.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarTest extends BackEndTest {
    private static final LocalDate DATE = LocalDate.parse("2022-07-01");

    @Test(groups = {"be", "calendar"})
    public void getCalendar() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin( userData);

        List<CalendarResponse> calendarResponses = CalendarActions.getCalendar(language, accessTokenId, DATE)
            .stream()
            .sorted(Comparator.comparing(CalendarResponse::getDate))
            .collect(Collectors.toList());

        assertThat(calendarResponses).hasSize(35);

        assertThat(calendarResponses.get(0).getDate()).isEqualTo(LocalDate.parse("2022-06-27"));
        assertThat(calendarResponses.get(34).getDate()).isEqualTo(LocalDate.parse("2022-07-31"));
    }
}
