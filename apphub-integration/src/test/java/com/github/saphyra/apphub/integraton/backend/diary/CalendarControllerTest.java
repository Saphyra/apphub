package com.github.saphyra.apphub.integraton.backend.diary;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.diary.CalendarActions;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.diary.CalendarResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarControllerTest extends BackEndTest {
    private static final LocalDate DATE = LocalDate.parse("2022-07-01");

    @Test(groups = "diary")
    public void getCalendar() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        List<CalendarResponse> calendarResponses = CalendarActions.getCalendar(language, accessTokenId, DATE)
            .stream()
            .sorted(Comparator.comparing(CalendarResponse::getDate))
            .collect(Collectors.toList());

        assertThat(calendarResponses).hasSize(35);

        assertThat(calendarResponses.get(0).getDate()).isEqualTo(LocalDate.parse("2022-06-27"));
        assertThat(calendarResponses.get(34).getDate()).isEqualTo(LocalDate.parse("2022-07-31"));
    }
}
