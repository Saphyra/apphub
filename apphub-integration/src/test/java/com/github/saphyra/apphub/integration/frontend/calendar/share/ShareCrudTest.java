package com.github.saphyra.apphub.integration.frontend.calendar.share;

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

public class ShareCrudTest extends SeleniumTest {
    @Test(groups = {"fe", "calendar"})
    public void shareCrud() {
        List<WebDriver> drivers = extractDrivers(2);

        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), drivers);
        WebDriver ownerDriver = contexts.get(0).driver();
        RegistrationParameters sharedWithUserData = contexts.get(1).userData();
        WebDriver sharedWithDriver = contexts.get(1).driver();

        //Create event
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event and verify event is shared
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);
        CalendarIndexPageActions.editEvent(ownerDriver);
        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
        CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW_CHILDREN);
        CalendarSharePageActions.share(ownerDriver);

        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));

        //Edit grants
        CalendarSharePageActions.findSharedWith(ownerDriver, sharedWithUserData.getEmail())
            .orElseThrow()
            .toggleGrant(Grant.VIEW)
            .save(ownerDriver);

        sharedWithDriver.navigate().refresh();
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(Constants.QUESTION_MARK + Constants.SHARED_SUFFIX));

        //Unshare
        CalendarSharePageActions.findSharedWith(ownerDriver, sharedWithUserData.getEmail())
            .orElseThrow()
            .unshare(ownerDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarSharePageActions.getSharedWith(ownerDriver)).isEmpty());

        sharedWithDriver.navigate().refresh();
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty());
    }
}
