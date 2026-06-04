package com.github.saphyra.apphub.integration.backend.calendar;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarEventActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarLabelActions;
import com.github.saphyra.apphub.integration.action.backend.calendar.CalendarOccurrenceActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.calendar.EventRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceRequest;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

public class CalendarRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "calendar", "role-protection"})
    public void calendarRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DynamoDbUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();


        //Labels
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getCreateLabelResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getGetLabelsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getGetLabelResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getDeleteLabelResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getEditLabelResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> CalendarLabelActions.getLabelsOfEventResponse(getServerPort(), accessToken, UUID.randomUUID()));

        //Events
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getCreateEventResponse(getServerPort(), accessToken, EventRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getGetEventsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getGetLabellessEventsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getGetEventResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getDeleteEventResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getEditEventResponse(getServerPort(), accessToken, UUID.randomUUID(), EventRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getExpiredEventsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getHideExpiredEventResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getExtendExpiredEventResponse(getServerPort(), accessToken, UUID.randomUUID(), LocalDate.now()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getMergeEventsResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getSearchResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> CalendarEventActions.getArchiveEventResponse(getServerPort(), accessToken, UUID.randomUUID(), false));

        //Occurrences
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getCreateOccurrenceResponse(getServerPort(), accessToken, UUID.randomUUID(), OccurrenceRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getEditOccurrenceResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), OccurrenceRequest.builder().build()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getDeleteOccurrenceResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getGetOccurrencesResponse(getServerPort(), accessToken, LocalDate.now(), LocalDate.now()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getGetOccurrenceResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getGetOccurrencesOfEventResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getEditOccurrenceStatusResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), OccurrenceStatus.DONE));
        CommonUtils.verifyMissingRole(() -> CalendarOccurrenceActions.getSetRemindedResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_CALENDAR},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
