package com.github.saphyra.apphub.integration.frontend.calendar;

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
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarDeleteOccurrenceAndEventTest extends SeleniumTest {
    /**
     * Tests deletion of a single occurrence and the entire event for a recurring calendar entry.
     *
     * <ul>
     *   <li>Creates a recurring event and verifies that all expected occurrences are present.</li>
     *   <li>Deletes one occurrence and checks that only that occurrence is removed while others remain.</li>
     *   <li>Deletes the whole event and confirms that no occurrences of the event exist.</li>
     * </ul>
     */
    @Test(groups = {"fe", "calendar"})
    public void deleteOccurrenceAndEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        CommonUtils.enableTestMode(driver);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.EVERY_X_DAYS);
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.setReferenceDate(driver, event.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate())).isNotEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS))).isNotEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS * 2))).isNotEmpty();
        });

        CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS), event.getTitle())
            .open(driver);

        CalendarIndexPageActions.deleteOpenedOccurrence(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate())).isNotEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS))).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS * 2))).isNotEmpty();
        });

        CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate(), event.getTitle())
            .open(driver);
        CalendarIndexPageActions.deleteEvent(driver);
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate())).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS))).isEmpty();
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate().plusDays(CreateEventParameters.DEFAULT_REPETITION_DATA_EVERY_X_DAYS * 2))).isEmpty();
        });
    }
}
