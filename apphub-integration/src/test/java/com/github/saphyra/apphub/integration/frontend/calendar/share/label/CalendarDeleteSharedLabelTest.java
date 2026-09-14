package com.github.saphyra.apphub.integration.frontend.calendar.share.label;

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
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarDeleteSharedLabelTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";

    @Test(groups = {"fe", "calendar"})
    void deleteSharedLabel() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), false);

        //Delete label
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .delete()
            .confirmDeletion(sharedWithDriver);

        //Verify label is deleted
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(sharedWithDriver)).isEmpty());

        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(ownerDriver)).isEmpty());
        CalendarLabelsPageActions.back(ownerDriver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getLabels(ownerDriver)).isEmpty());
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(ownerDriver, parameters.getStartDate()));
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOccurrencesOnDate(ownerDriver, parameters.getStartDate())).hasSize(1));
    }

    @Test(groups = {"fe", "calendar"})
    public void deleteSharedLabel_noGrant() {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        //Create event with label
        CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        //Share label
        shareLabel(ownerDriver, testContext.sharedWithUserData().getEmail(), true);

        //Delete label
        CalendarIndexPageActions.toLabelsPage(sharedWithDriver);

        CalendarLabelsPageActions.getLabel(sharedWithDriver, LABEL_1 + Constants.SHARED_SUFFIX)
            .delete()
            .confirmDeletion(sharedWithDriver);

        //Verify label is not deleted
        CalendarSharePageActions.back(ownerDriver);
        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(ownerDriver)).extracting(CalendarLabel::getLabel).containsExactly(LABEL_1));

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarLabelsPageActions.getLabels(sharedWithDriver)).extracting(CalendarLabel::getLabel).containsExactly(LABEL_1 + Constants.SHARED_SUFFIX));
    }

    private void shareLabel(WebDriver ownerDriver, String sharedWithEmail, boolean removeDeleteGrant) {
        CalendarFlow.shareLabel(ownerDriver, sharedWithEmail, driver -> {
            CalendarSharePageActions.selectAllGrants(driver);
            if (removeDeleteGrant) {
                CalendarSharePageActions.toggleGrant(driver, Grant.DELETE);
            }
        });
    }
}
