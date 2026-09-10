package com.github.saphyra.apphub.integration.frontend.calendar.share.occurrence;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarFlow;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarOccurrencePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarSharePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.structure.api.calendar.Grant;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarViewSharedOccurrenceTest extends SeleniumTest {
    private static final String LABEL_1 = "label-1";
    private static final String NOTE = "note";

    @Test(dataProvider = "visibilityCases", groups = {"fe", "calendar"})
    public void sharedOccurrenceVisibility(boolean shareOccurrence, boolean expectedNoteVisible) {
        CalendarFlow.TestContext testContext = CalendarFlow.initContext(getServerPort(), extractDrivers(2));
        WebDriver ownerDriver = testContext.ownerDriver();
        WebDriver sharedWithDriver = testContext.sharedWithDriver();

        CreateEventParameters parameters = CalendarFlow.createEvent(ownerDriver, List.of(LABEL_1), null);

        CalendarFlow.shareEvent(ownerDriver, parameters.getStartDate(), testContext.sharedWithUserData().getEmail(), driver -> {
            CalendarSharePageActions.toggleGrant(driver, Grant.VIEW);
            CalendarSharePageActions.toggleGrant(driver, Grant.SEE_CHILDREN);
        });
        CalendarSharePageActions.back(ownerDriver);
        CalendarEventPageActions.backFromEdit(ownerDriver);

        //Edit and optionally share occurrence.
        openOccurrenceForEdit(ownerDriver, parameters.getStartDate());
        if (shareOccurrence) {
            CalendarOccurrencePageActions.share(ownerDriver);
            CalendarSharePageActions.selectUser(ownerDriver, testContext.sharedWithUserData().getEmail());
            CalendarSharePageActions.toggleGrant(ownerDriver, Grant.VIEW);
            CalendarSharePageActions.share(ownerDriver);
            CalendarSharePageActions.back(ownerDriver);
        }
        CalendarOccurrencePageActions.setNote(ownerDriver, NOTE);
        CalendarOccurrencePageActions.save(ownerDriver);

        //View occurrence
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(sharedWithDriver, parameters.getStartDate()));
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(sharedWithDriver, parameters.getStartDate()))
            .open(sharedWithDriver);

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOpenedOccurrenceTitle(sharedWithDriver)).isEqualTo(parameters.getTitle());
            if (expectedNoteVisible) {
                assertThat(CalendarIndexPageActions.getOpenedOccurrenceNote(sharedWithDriver)).contains(NOTE);
            } else {
                assertThat(CalendarIndexPageActions.getOpenedOccurrenceNote(sharedWithDriver)).isEmpty();
            }
        });
    }

    @DataProvider
    private Object[][] visibilityCases() {
        return new Object[][]{
            new Object[]{false, false},
            new Object[]{true, true},
        };
    }

    private void openOccurrenceForEdit(WebDriver driver, java.time.LocalDate date) {
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.setReferenceDate(driver, date));
        AwaitilityWrapper.getSingleItemFromListWithWait(() -> CalendarIndexPageActions.getOccurrencesOnDate(driver, date))
            .open(driver);
        AwaitilityWrapper.retry(() -> CalendarIndexPageActions.editOpenedOccurrence(driver));
    }
}
