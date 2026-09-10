package com.github.saphyra.apphub.integration.frontend.calendar.share.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarOccurrencePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarShareIndexPageTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    void sharedLabel() {
        List<WebDriver> drivers = extractDrivers(2);

        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), drivers);
        WebDriver ownerDriver = contexts.get(0).driver();
        RegistrationParameters sharedWithUserData = contexts.get(1).userData();
        WebDriver sharedWithDriver = contexts.get(1).driver();

        //Create event with label
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share label
        CalendarIndexPageActions.toLabelsPage(ownerDriver);

        AwaitilityWrapper.getListWithWait(() -> CalendarLabelsPageActions.getLabels(ownerDriver), l -> !l.isEmpty())
            .getFirst()
            .share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Verify shared label displayed
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).extracting(CalendarOccurrence::isShared).containsExactly(true));
    }

    @Test(groups = {"fe", "calendar"})
    void sharedEvent() {
        List<WebDriver> drivers = extractDrivers(2);

        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), drivers);
        WebDriver ownerDriver = contexts.get(0).driver();
        RegistrationParameters sharedWithUserData = contexts.get(1).userData();
        WebDriver sharedWithDriver = contexts.get(1).driver();

        //Create event to share
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME)
            .toBuilder()
            .newLabels(List.of(LABEL_1))
            .build();
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share event
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);
        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.getOpenedOccurrenceTitle(ownerDriver).equals(parameters.getTitle()))
            .assertTrue("Occurrence is not opened");
        CalendarIndexPageActions.editEvent(ownerDriver);

        CalendarEventPageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Verify shared event displayed
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).extracting(CalendarOccurrence::isShared).containsExactly(true));
    }

    @Test(groups = {"fe", "calendar"})
    void sharedOccurrence() {
        List<WebDriver> drivers = extractDrivers(2);

        List<CalendarFlow.Context> contexts = CalendarFlow.init(getServerPort(), drivers);
        WebDriver ownerDriver = contexts.get(0).driver();
        RegistrationParameters sharedWithUserData = contexts.get(1).userData();
        WebDriver sharedWithDriver = contexts.get(1).driver();

        //Create event to share
        CalendarIndexPageActions.openCreateEventPage(ownerDriver);

        CreateEventParameters parameters = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(ownerDriver, parameters);
        CalendarEventPageActions.create(ownerDriver);
        ToastMessageUtil.verifySuccessToast(ownerDriver, LocalizedText.CALENDAR_EVENT_CREATED);

        //Share occurrence
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()))
            .open(ownerDriver);
        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.getOpenedOccurrenceTitle(ownerDriver).equals(parameters.getTitle()))
            .assertTrue("Occurrence is not opened");
        CalendarIndexPageActions.editOpenedOccurrence(ownerDriver);

        CalendarOccurrencePageActions.share(ownerDriver);

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithUserData.getEmail());
        CalendarSharePageActions.selectAllGrants(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);

        //Verify shared event displayed
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).extracting(CalendarOccurrence::isShared).containsExactly(true));
    }
}
