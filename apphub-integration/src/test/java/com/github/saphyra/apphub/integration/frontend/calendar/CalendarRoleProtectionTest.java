package com.github.saphyra.apphub.integration.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.framework.endpoints.CalendarEndpoints;
import com.github.saphyra.apphub.integration.structure.api.calendar.SharedObjectType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;
import java.util.UUID;

public class CalendarRoleProtectionTest extends SeleniumTest {
    @Test(dataProvider = "roleDataProvider", groups = {"fe", "calendar", "role-protection"})
    public void calendarRoleProtection(String role) {
        WebDriver driver = extractDriver();

        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        UserDynamoDbRepository.removeRoleByEmail(userData.getEmail(), role);
        AccessTokenActions.invalidateAccessToken(driver, getServerPort());

        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_LABELS_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_EXPIRED_EVENTS_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_SEARCH_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_CREATE_EVENT_PAGE);
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_EDIT_EVENT_PAGE, Map.of("eventId", UUID.randomUUID()));
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_EDIT_OCCURRENCE_PAGE, Map.of("eventId", UUID.randomUUID(), "occurrenceId", UUID.randomUUID()));
        CommonUtils.verifyMissingRole(getServerPort(), driver, CalendarEndpoints.CALENDAR_SHARE_PAGE, Map.of("type", SharedObjectType.OCCURRENCE, "eventId", UUID.randomUUID(), "occurrenceId", UUID.randomUUID()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleDataProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_CALENDAR},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
