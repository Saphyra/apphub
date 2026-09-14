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
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarViewSharedEventTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(dataProvider = "eventShareCases", groups = {"fe", "calendar"})
    void eventShareVisibility(Grant firstGrant, Grant secondGrant, boolean seeOnly) {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        CreateEventParameters parameters = CalendarFlow.createEvent(testContext.ownerDriver(), List.of(LABEL_1), null);

        CalendarFlow.shareEvent(testContext.ownerDriver(), parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), ownerDriver -> {
            CalendarSharePageActions.toggleGrant(ownerDriver, firstGrant);
            CalendarSharePageActions.toggleGrant(ownerDriver, secondGrant);
        });

        assertOccurrenceTitle(testContext.sharedWithDriver(), parameters, seeOnly);
    }

    @Test(dataProvider = "labelShareCases", groups = {"fe", "calendar"})
    void sharedLabelVisibility(Grant childGrant, boolean seeOnly) {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        CreateEventParameters parameters = CalendarFlow.createEvent(testContext.ownerDriver(), List.of(LABEL_1), null);

        CalendarFlow.shareLabel(testContext.ownerDriver(), testContext.sharedWithUserData().getEmail(), ownerDriver -> {
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
            CalendarSharePageActions.toggleGrant(ownerDriver, childGrant);
        });

        assertOccurrenceTitle(testContext.sharedWithDriver(), parameters, seeOnly);
        assertSharedLabelEventTitle(testContext.sharedWithDriver(), seeOnly ? Constants.QUESTION_MARK + Constants.SHARED_SUFFIX : parameters.getTitle() + Constants.SHARED_SUFFIX);
    }

    @Test(dataProvider = "labelAndEventShareCases", groups = {"fe", "calendar"})
    void sharedLabelAndEventVisibility(Grant labelChildGrant, Grant eventGrant) {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        CreateEventParameters parameters = CalendarFlow.createEvent(testContext.ownerDriver(), List.of(LABEL_1), null);

        CalendarFlow.shareLabel(testContext.ownerDriver(), testContext.sharedWithUserData().getEmail(), ownerDriver -> {
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
            CalendarSharePageActions.toggleGrant(ownerDriver, labelChildGrant);
        });

        shareEventFromLabelPage(testContext.ownerDriver(), parameters.getTitle(), testContext.sharedWithUserData().getEmail(), ownerDriver -> CalendarSharePageActions.toggleGrant(ownerDriver, eventGrant));

        assertOccurrenceTitle(testContext.sharedWithDriver(), parameters, false);
        assertSharedLabelEventTitle(testContext.sharedWithDriver(), parameters.getTitle() + Constants.SHARED_SUFFIX);
    }

    @DataProvider
    private Object[][] eventShareCases() {
        return new Object[][]{
            new Object[]{Grant.VIEW, Grant.VIEW_CHILDREN, false},
            new Object[]{Grant.SEE, Grant.SEE_CHILDREN, true},
        };
    }

    @DataProvider
    private Object[][] labelShareCases() {
        return new Object[][]{
            new Object[]{Grant.VIEW_CHILDREN, false},
            new Object[]{Grant.SEE_CHILDREN, true},
        };
    }

    @DataProvider
    private Object[][] labelAndEventShareCases() {
        return new Object[][]{
            new Object[]{Grant.SEE_CHILDREN, Grant.VIEW},
            new Object[]{Grant.VIEW_CHILDREN, Grant.SEE},
        };
    }

    private void shareEventFromLabelPage(WebDriver ownerDriver, String eventTitle, String sharedWithEmail, Consumer<WebDriver> grantSetup) {
        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(ownerDriver, eventTitle))
            .orElseThrow()
            .click();

        AwaitilityWrapper.retry(() -> CalendarLabelsPageActions.editOpenedEvent(ownerDriver));
        AwaitilityWrapper.retry(() -> CalendarEventPageActions.share(ownerDriver));

        CalendarSharePageActions.selectUser(ownerDriver, sharedWithEmail);
        grantSetup.accept(ownerDriver);
        CalendarSharePageActions.share(ownerDriver);
    }

    private void assertOccurrenceTitle(WebDriver sharedWithDriver, CreateEventParameters parameters, boolean seeOnly) {
        String expectedTitle = seeOnly
            ? Constants.QUESTION_MARK + Constants.SHARED_SUFFIX
            : parameters.getTitle() + Constants.SHARED_SUFFIX;

        CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .extracting(CalendarOccurrence::getTitle)
            .containsExactly(expectedTitle));
    }

    private void assertSharedLabelEventTitle(WebDriver sharedWithDriver, String expectedTitle) {
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .open();
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver))
            .extracting(WebElement::getText)
            .containsExactly(expectedTitle));
    }

}
