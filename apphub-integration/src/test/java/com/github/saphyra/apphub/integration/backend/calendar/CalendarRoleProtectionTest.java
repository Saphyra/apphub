package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

public class CalendarRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "calendar"})
    public void calendarRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Labels
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getGetLabelsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getGetLabelResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getDeleteLabelResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getEditLabelResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getLabelsOfEventResponse(getServerPort(), accessTokenId, UUID.randomUUID()));

        //Events
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getCreateEvemtResponse(getServerPort(), accessTokenId, EventRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getGetEventsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getGetEventResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getDeleteEventResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getEditEventResponse(getServerPort(), accessTokenId, UUID.randomUUID(), EventRequest.builder().build()));

        //Occurrences
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessTokenId, UUID.randomUUID(), OccurrenceRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessTokenId, UUID.randomUUID(), OccurrenceRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getDeleteOccurrenceResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getGetOccurrencesResponse(getServerPort(), accessTokenId, LocalDate.now(), LocalDate.now()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getGetOccurrenceResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getGetOccurrencesOfEventResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getEditOccurrenceStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), OccurrenceStatus.DONE));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getSetRemindedResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_CALENDAR},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
