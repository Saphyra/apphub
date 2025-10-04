package com.github.saphyra.apphub.integration.frontend.calendar.event;

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
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CalendarEditEventTest extends SeleniumTest {
    @Test(groups = {"fe", "calendar"})
    public void editEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.ONE_TIME);
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);

        CalendarIndexPageActions.setReferenceDate(driver, event.getStartDate());
        AwaitilityWrapper.getWithWait(() -> CalendarIndexPageActions.findOccurrenceByTitleOnDateValidated(driver, event.getStartDate(), event.getTitle()))
            .orElseThrow(() -> new IllegalStateException("Occurrence not found"))
            .open();
        CalendarIndexPageActions.editEvent(driver);

        CreateEventParameters updatedEvent = CreateEventParameters.edit(RepetitionType.EVERY_X_DAYS);
        CalendarEventPageActions.fillForm(driver, updatedEvent);
        CalendarEventPageActions.save(driver);
        CalendarEventPageActions.confirmSave(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_SAVED);
        CalendarEventPageActions.backFromEdit(driver);

        CalendarIndexPageActions.setReferenceDate(driver, updatedEvent.getStartDate());
        AwaitilityWrapper.awaitAssert(() -> {
            assertThat(CalendarIndexPageActions.getOccurrencesOnDate(driver, updatedEvent.getStartDate()))
                .hasSize(1)
                .extracting(CalendarOccurrence::getTitle)
                .containsExactly(updatedEvent.getTitle());
            assertThat(CalendarIndexPageActions.getDays(driver).stream().mapToLong(calendarDate -> calendarDate.getOccurrences().size()).sum()).isGreaterThan(1);
        });
    }
}
