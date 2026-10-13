package com.github.saphyra.apphub.integration.frontend.calendar.occurrence;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateOccurrenceTest extends SeleniumTest {
    private static final String NOTE = "note";

    @Test(groups = {"fe", "calendar"})
    public void createOccurrence() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS);
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.setReferenceDate(driver, event.getStartDate());
        AwaitilityWrapper.getWithWait(() -> CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate(), event.getTitle()))
            .orElseThrow(() -> new IllegalStateException("Occurrence not found"))
            .open(driver);
        CalendarIndexPageActions.editEvent(driver);

        WebElementUtils.waitForSpinnerToDisappear(driver);

        CalendarEventPageActions.setCreateOccurrenceDate(driver, event.getStartDate().plusDays(1));
        CalendarEventPageActions.setCreateOccurrenceNote(driver, NOTE);
        CalendarEventPageActions.createOccurrence(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_OCCURRENCE_CREATED);
        CalendarEventPageActions.backFromEdit(driver);

        AwaitilityWrapper.getWithWait(() -> CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate().plusDays(1), event.getTitle()))
            .orElseThrow(() -> new IllegalStateException("Occurrence not found"))
            .open(driver);

        AwaitilityWrapper.awaitAssert(() -> assertThat(CalendarIndexPageActions.getOpenedOccurrenceNote(driver)).contains(NOTE));
    }
}
