package com.github.saphyra.apphub.integration.frontend.calendar.share.occurrence;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarOccurrencePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEditSharedOccurrenceTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(dataProvider = "editableShareCases", groups = {"fe", "calendar"})
    void editSharedOccurrence(ShareTarget shareTarget) {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        boolean withLabel = shareTarget != ShareTarget.OCCURRENCE;
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, withLabel ? List.of(LABEL_1) : null, null);
        shareEditableOccurrence(ownerDriver, testContext.sharedWithUserData().getEmail(), parameters, shareTarget);

        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newOccurrenceDate)).hasSize(1));

        backFromShare(ownerDriver, shareTarget);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).hasSize(1));
    }

    @DataProvider
    private Object[][] editableShareCases() {
        return new Object[][]{
            new Object[]{ShareTarget.OCCURRENCE},
            new Object[]{ShareTarget.EVENT},
            new Object[]{ShareTarget.LABEL},
        };
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedOccurrence_noEditGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share occurrence
        CalendarFlow.shareOccurrence(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.EDIT);
        });

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarOccurrencePageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedOccurrence_noViewGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share occurrence
        CalendarFlow.shareOccurrence(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.VIEW);
        });

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarOccurrencePageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedEvent_noEditGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share event
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.EDIT_CHILDREN);
        });

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedEvent_noViewGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share event
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.VIEW_CHILDREN);
        });

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedLabel_noEditGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.EDIT_CHILDREN);
        });

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(groups = {"fe", "calendar"})
    public void editOccurrenceOfSharedLabel_noViewGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.VIEW_CHILDREN);
        });

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence not edited
        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).isEmpty());
    }

    @Test(dataProvider = "lego", groups = {"fe", "calendar"})
    public void editSharedOccurrence_legoGrants(Grant labelGrant, Grant eventGrant, Grant occurrenceGrant) {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        if(nonNull(labelGrant)){
                CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), driver -> {
                CalendarSharePageActions.toggleGrant(driver, Grant.VIEW);
                CalendarSharePageActions.toggleGrant(driver, Grant.SEE_CHILDREN);
                CalendarSharePageActions.toggleGrant(driver, labelGrant);
            });

            CalendarSharePageActions.back(ownerDriver);
            CalendarLabelsPageActions.back(ownerDriver);
        }

        //Share event
        if(nonNull(eventGrant)){
                CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
                CalendarSharePageActions.toggleGrant(driver, Grant.VIEW);
                CalendarSharePageActions.toggleGrant(driver, Grant.SEE_CHILDREN);
                CalendarSharePageActions.toggleGrant(driver, eventGrant);
            });

            CalendarSharePageActions.back(ownerDriver);
            CalendarEventPageActions.backFromEdit(ownerDriver);
        }

        if(nonNull(occurrenceGrant)){
            CalendarFlow.shareOccurrence(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> CalendarSharePageActions.toggleGrant(driver, occurrenceGrant));

            CalendarSharePageActions.back(ownerDriver);
            CalendarOccurrencePageActions.back(ownerDriver);
        }

        //Edit occurrence
        LocalDate newOccurrenceDate = editOccurrenceDate(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence edited
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, newOccurrenceDate)).hasSize(1));

        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, newOccurrenceDate));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, newOccurrenceDate)).hasSize(1));
    }

    @DataProvider(parallel = true)
    private static Object[][] lego() {
        return new Object[][]{
            new Object[]{Grant.EDIT_CHILDREN, Grant.VIEW_CHILDREN, null},
            new Object[]{Grant.EDIT_CHILDREN, null, Grant.VIEW},
            new Object[]{Grant.VIEW_CHILDREN, null, Grant.EDIT},
            new Object[]{Grant.VIEW_CHILDREN, Grant.EDIT_CHILDREN, null},
            new Object[]{null, Grant.VIEW_CHILDREN, Grant.EDIT},
            new Object[]{null, Grant.EDIT_CHILDREN, Grant.VIEW},
        };
    }

    private void shareEditableOccurrence(WebDriver ownerDriver, String sharedWithEmail, CreateEventParameters parameters, ShareTarget shareTarget) {
        switch (shareTarget) {
            case OCCURRENCE -> CalendarFlow.shareOccurrence(ownerDriver, parameters.getStartDate(), sharedWithEmail, CalendarSharePageActions::selectAllGrants);
            case EVENT -> CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), sharedWithEmail, CalendarSharePageActions::selectAllGrants);
            case LABEL -> CalendarFlow.shareLabel(ownerDriver, sharedWithEmail, CalendarSharePageActions::selectAllGrants);
        }
    }

    private void backFromShare(WebDriver ownerDriver, ShareTarget shareTarget) {
        CalendarSharePageActions.back(ownerDriver);
        switch (shareTarget) {
            case OCCURRENCE -> CalendarOccurrencePageActions.back(ownerDriver);
            case EVENT -> CalendarEventPageActions.backFromEdit(ownerDriver);
            case LABEL -> CalendarLabelsPageActions.back(ownerDriver);
        }
    }

    private LocalDate editOccurrenceDate(WebDriver sharedWithDriver, LocalDate date) {
        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, date);
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, date))
            .open(sharedWithDriver);
        CalendarIndexPageActions.editOpenedOccurrence(sharedWithDriver);

        LocalDate newOccurrenceDate = date.plusDays(1);
        CalendarOccurrencePageActions.setDate(sharedWithDriver, newOccurrenceDate);
        CalendarOccurrencePageActions.save(sharedWithDriver);
        return newOccurrenceDate;
    }


    private enum ShareTarget {
        OCCURRENCE,
        EVENT,
        LABEL
    }
}
