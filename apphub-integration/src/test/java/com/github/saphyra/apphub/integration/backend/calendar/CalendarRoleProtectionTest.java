package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarSearchActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.EventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.OccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.calendar.CreateEventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.EditOccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.ReferenceDate;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

public class CalendarRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "calendar"})
    public void calendarRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        CommonUtils.verifyMissingRole(() -> CalendarActions.getCalendarResponse(accessTokenId, LocalDate.now()));
        CommonUtils.verifyMissingRole(() -> CalendarSearchActions.getSearchResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> EventActions.getCreateEventResponse(accessTokenId, new CreateEventRequest()));
        CommonUtils.verifyMissingRole(() -> EventActions.getDeleteEventResponse(accessTokenId, UUID.randomUUID(), new ReferenceDate()));
        CommonUtils.verifyMissingRole(() -> OccurrenceActions.getEditOccurrenceResponse(accessTokenId, UUID.randomUUID(), new EditOccurrenceRequest()));
        CommonUtils.verifyMissingRole(() -> OccurrenceActions.getMarkOccurrenceDoneResponse(accessTokenId, UUID.randomUUID(), new ReferenceDate()));
        CommonUtils.verifyMissingRole(() -> OccurrenceActions.getMarkOccurrenceSnoozedResponse(accessTokenId, UUID.randomUUID(), new ReferenceDate()));
        CommonUtils.verifyMissingRole(() -> OccurrenceActions.getMarkOccurrenceDefaultResponse(accessTokenId, UUID.randomUUID(), new ReferenceDate()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_CALENDAR},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
