package com.github.saphyra.apphub.integration.frontend.calendar.share.label;

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
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarLabel;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEditSharedLabelTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";
    private static final String LABEL_2 = "label-2";
    private static final String EVENT_TITLE_2 = "event-title-2";

    @Test(groups = {"fe", "calendar"})
    void editSharedLabel() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), null);

        //Edit label
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .edit()
            .newLabel(sharedWithDriver, LABEL_2)
            .confirmNewLabel(sharedWithDriver);

        //Verify label edited
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(sharedWithDriver)).extracting(CalendarLabel::getLabel).containsExactly(LABEL_2 + Constants.SHARED_SUFFIX));

        CalendarSharePageActions.back(ownerDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(ownerDriver)).extracting(CalendarLabel::getLabel).containsExactly(LABEL_2));
    }

    @Test(groups = {"fe", "calendar"})
    void editSharedLabelEvent() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), null);

        //Edit event
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, parameters.getTitle() + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarLabelsPageActions.getOpenedEventTitle(sharedWithDriver).equals(parameters.getTitle() + Constants.SHARED_SUFFIX))
            .assertTrue("Event not opened");

        CalendarLabelsPageActions.editOpenedEvent(sharedWithDriver);

        CreateEventParameters editedParameters = parameters.toBuilder()
            .title(EVENT_TITLE_2)
            .build();
        CalendarEventPageActions.fillForm(sharedWithDriver, editedParameters);
        CalendarEventPageActions.save(sharedWithDriver);
        CalendarEventPageActions.confirmSave(sharedWithDriver);

        //Verify event edited
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).extracting(WebElement::getText).containsExactly(EVENT_TITLE_2 + Constants.SHARED_SUFFIX));

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getLabel(ownerDriver, LABEL_1))
            .orElseThrow()
            .open();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(ownerDriver)).extracting(WebElement::getText).containsExactly(EVENT_TITLE_2));
    }

    @Test(groups = {"fe", "calendar"})
    public void editSharedLabel_noGrant(){
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), Grant.EDIT);

        //Edit label
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .edit()
            .newLabel(sharedWithDriver, LABEL_2)
            .confirmNewLabel(sharedWithDriver);

        //Verify label is not edited
        CalendarSharePageActions.back(ownerDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(ownerDriver)).extracting(CalendarLabel::getLabel).containsExactly(LABEL_1));
    }

    private void shareLabel(WebDriver ownerDriver, String sharedWithEmail, Grant removedGrant) {
        CalendarFlow.shareLabel(ownerDriver, sharedWithEmail, driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            if (removedGrant != null) {
                CalendarSharePageActions.toggleGrant(driver, removedGrant);
            }
        });
    }
}
