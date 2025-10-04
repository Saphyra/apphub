package com.github.saphyra.apphub.integration.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarOccurrencePageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.calendar.OccurrenceParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.OccurrenceStatus;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class EditOccurrenceTest extends SeleniumTest {
    private static final LocalTime NEW_OCCURRENCE_TIME = LocalTime.of(15, 30);
    private static final String NEW_NOTE = "new-note";

    @Test(groups = {"fe", "calendar"})
    public void editOccurrence() {
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
            .open();
        CalendarIndexPageActions.editOccurrence(driver);

        OccurrenceParameters occurrence = OccurrenceParameters.builder()
            .date(event.getStartDate().plusDays(1))
            .time(NEW_OCCURRENCE_TIME)
            .status(OccurrenceStatus.SNOOZED)
            .note(NEW_NOTE)
            .remindMeBeforeDays(2)
            .reminded(false)
            .build();

        //Empty date
        CalendarOccurrencePageActions.fill(driver, occurrence.toBuilder().date(null).build());
        CalendarOccurrencePageActions.save(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_EMPTY_DATE);

        //Edit
        CalendarOccurrencePageActions.fill(driver, occurrence);
        CalendarOccurrencePageActions.save(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_OCCURRENCE_SAVED);

        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate().minusDays(1), event.getTitle()).getStatus())
                .isEqualTo(OccurrenceStatus.REMINDER);
            assertThat(CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate().plusDays(1), event.getTitle()).getStatus())
                .isEqualTo(OccurrenceStatus.SNOOZED);
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, event.getStartDate())).isEmpty();
        });
    }
}
