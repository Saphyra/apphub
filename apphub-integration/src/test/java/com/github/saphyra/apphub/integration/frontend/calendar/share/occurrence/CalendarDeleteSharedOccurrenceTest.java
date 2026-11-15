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
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarDeleteSharedOccurrenceTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    /**
     * Tests deletion of a shared label occurrence when the user has delete permissions.
     * <ul>
     *   <li>Owner creates an event with label "label-1" and shares the label with all grants.</li>
     *   <li>The shared user deletes the occurrence on its start date.</li>
     *   <li>Verify that the occurrence is removed for both users.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    void deleteSharedLabelOccurrence() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        //Delete occurrence
        CalendarFlow.deleteOccurrence(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty());

        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).isEmpty());
    }

    /**
     * Tests that a shared label occurrence cannot be deleted without DELETE_CHILDREN grant.
     * <ul>
     *   <li>Owner creates an event with label "label-1" and shares the label with all grants except DELETE_CHILDREN.</li>
     *   <li>The shared user attempts to delete the occurrence on its start date.</li>
     *   <li>Verify that the occurrence remains for both users.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    void deleteSharedLabelOccurrence_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE_CHILDREN);
        });

        //Delete occurrence
        CalendarFlow.deleteOccurrence(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence is not deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).hasSize(1));

        CalendarSharePageActions.back(ownerDriver);
        CalendarLabelsPageActions.back(ownerDriver);
        CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }

    /**
     * Tests deletion of a recurring event occurrence when the user has all grants.
     * <ul>
     *   <li>Owner creates a recurring event with label "label-1" and shares the event with all grants.</li>
     *   <li>The shared user deletes the first occurrence on its start date.</li>
     *   <li>Verify that the deleted occurrence is removed, but subsequent occurrences remain for both users.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    public void deleteSharedEventOccurrence() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), 2);

        //Share event
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);

        //Delete occurrence
        CalendarFlow.deleteOccurrence(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence deleted
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate().plusDays(1))).hasSize(1);
        });

        ownerDriver.navigate().refresh();

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate().plusDays(1))).hasSize(1);
        });
    }

    /**
     * Tests that a recurring event occurrence cannot be deleted without DELETE_CHILDREN grant.
     * <ul>
     *   <li>Owner creates a recurring event with label "label-1" and shares the event with all grants except DELETE_CHILDREN.</li>
     *   <li>The shared user attempts to delete the first occurrence on its start date.</li>
     *   <li>Verify that the occurrence remains for both users.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    public void deleteSharedEventOccurrence_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share event
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE_CHILDREN);
        });

        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);

        //Delete occurrence
        CalendarFlow.deleteOccurrence(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence deleted
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).hasSize(1);
        });

        ownerDriver.navigate().refresh();

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1);
        });
    }

    /**
     * Tests deletion of a single (non-recurring) event occurrence when the user has all grants.
     * <ul>
     *   <li>Owner creates a non-recurring event and shares the occurrence with all grants.</li>
     *   <li>The shared user deletes the occurrence on its start date.</li>
     *   <li>Verify that the occurrence is removed for both users.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    public void deleteSharedOccurrence() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share occurrence
        CalendarFlow.shareOccurrence(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        CalendarSharePageActions.back(ownerDriver);

        //Delete occurrence
        CalendarFlow.deleteOccurrence(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).isEmpty());

        CalendarOccurrencePageActions.back(ownerDriver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate()).isEmpty())
            .assertTrue();
    }

    /**
     * Tests that a single (non-recurring) event occurrence cannot be deleted without DELETE grant.
     * <ul>
     *   <li>Owner creates a non-recurring event and shares the occurrence with all grants except DELETE.</li>
     *   <li>The shared user attempts to delete the occurrence on its start date.</li>
     *   <li>Verify that the occurrence remains for both users.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    public void deleteSharedOccurrence_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share occurrence
        CalendarFlow.shareOccurrence(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            CalendarSharePageActions.toggleGrant(driver, Grant.DELETE);
        });

        CalendarSharePageActions.back(ownerDriver);

        //Delete occurrence
        CalendarFlow.deleteOccurrence(sharedWithDriver, parameters.getStartDate());

        //Verify occurrence deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate())).hasSize(1));

        CalendarOccurrencePageActions.back(ownerDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }
}
