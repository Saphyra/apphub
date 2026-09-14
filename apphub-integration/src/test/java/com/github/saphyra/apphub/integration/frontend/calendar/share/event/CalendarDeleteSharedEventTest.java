package com.github.saphyra.apphub.integration.frontend.calendar.share.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarDeleteSharedEventTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    void deleteSharedEvent_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE);
        });

        CalendarSharePageActions.back(ownerDriver);

        //Delete event
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.deleteEvent(sharedWithDriver);

        //Verify event is not deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).hasSize(1));

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    void deleteEventOfSharedLabel_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE_CHILDREN);
        });

        //Delete event
        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is not deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).hasSize(1));

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).hasSize(1));

        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    void deleteSharedEventOfSharedLabel_onlyLabelHasGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        //Share event
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);

        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE);
        });

        CalendarSharePageActions.back(ownerDriver);

        //Delete event
        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).isEmpty());

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    void deleteSharedEventOfSharedLabel_onlyEventHasGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE_CHILDREN);
        });

        //Share event
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);

        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        CalendarSharePageActions.back(ownerDriver);

        //Delete event
        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).isEmpty());

        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    void deleteSharedLabelEvent() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        //Delete event
        CalendarFlow.openSharedLabelEvent(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX, parameters.getTitle() + Constants.SHARED_SUFFIX);

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.deleteOpenedEvent(sharedWithDriver));

        //Verify event is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).isEmpty());

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).isEmpty();

        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void deleteSharedEvent() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);

        //Delete event
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());

        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        CalendarIndexPageActions.deleteEvent(sharedWithDriver);

        //Verify event deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty());

        ownerDriver.navigate().refresh();

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()).isEmpty())
            .assertTrue();
    }

}
