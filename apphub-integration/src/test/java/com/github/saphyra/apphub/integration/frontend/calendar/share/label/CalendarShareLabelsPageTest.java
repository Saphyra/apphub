package com.github.saphyra.apphub.integration.frontend.calendar.share.label;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarLabelsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOpenedEventOccurrence;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarShareLabelsPageTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    void sharedLabel() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        CalendarFlow.shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        //Verify label and event are shared
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .open();

        AwaitilityWrapper.getWithWait(() -> CalendarLabelsPageActions.getEvent(sharedWithDriver, parameters.getTitle() + Constants.SHARED_SUFFIX))
            .orElseThrow()
            .click();

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getOpenedEventTitle(sharedWithDriver)).isEqualTo(parameters.getTitle() + Constants.SHARED_SUFFIX));

        //Verify occurrence is shared
        CalendarOpenedEventOccurrence occurrence = AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarLabelsPageActions.getOpenedEventOccurrences(sharedWithDriver));

        assertThat(occurrence.isShared()).isTrue();

        occurrence.open();

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarLabelsPageActions.getOpenedOccurrenceDate(sharedWithDriver)).isEqualTo(parameters.getStartDate());
            assertThat(CalendarLabelsPageActions.isOpenedOccurrenceShared(sharedWithDriver)).isTrue();
        });
    }

    @Test(groups = {"fe", "calendar"})
    void sharedLabellessEvent() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, null, null);

        //Share event
        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), CalendarSharePageActions::selectAllGrants);

        //Verify event is shared
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);
        CalendarLabelsPageActions.selectNoLabelFilter(sharedWithDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getEvents(sharedWithDriver)).extracting(WebElement::getText).containsExactly(parameters.getTitle() + Constants.SHARED_SUFFIX));
    }

}
