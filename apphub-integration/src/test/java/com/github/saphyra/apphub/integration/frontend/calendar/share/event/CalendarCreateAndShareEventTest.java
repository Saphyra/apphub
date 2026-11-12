package com.github.saphyra.apphub.integration.frontend.calendar.share.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarCreateAndShareEventTest extends SeleniumTest {
    /**
     * Creates an event and shares it with another user, then verifies that the shared user can see the event.
     *
     * <ul>
     *   <li>validate that shared user sees event after sharing</li>
     * </ul>
     */

    @Test(groups = {"fe", "calendar"})
    public void createAndShareEvent() {
        // Setup two drivers
        List<WebDriver> drivers = extractDrivers(2);
        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), drivers);
        WebDriver ownerDriver = contexts.get(0).driver();
        RegistrationParameters sharedWithUserData = contexts.get(1).userData();
        WebDriver sharedWithDriver = contexts.get(1).driver();

        // Create event on selected date
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);
        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);

        // Before saving, search for user and set grants
        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.shareNewEvent(ownerDriver);

        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        // Verify sharedWith user can see the event
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
                .extracting(CalendarOccurrence::getTitle)
                .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));
    }
}
