package com.github.saphyra.apphub.integration.frontend.calendar.event;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarEventPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarExpiredEventsPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarIndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CreateEventParameters;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.*;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

public class ExpiredEventTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate START_DATE = CURRENT_DATE.minusDays(3);
    private static final LocalDate END_DATE = CURRENT_DATE.plusDays(3);

    @Test(groups = {"fe", "calendar"})
    public void hideExpiredEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        createExpiredEvent(driver);

        CalendarIndexPageActions.toExpiredEventsPage(driver);

        AwaitilityWrapper.getListWithWait(() -> CalendarExpiredEventsPageActions.getEvents(driver), webElements -> !webElements.isEmpty())
            .getFirst()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(driver, By.id("calendar-opened-event")).isPresent())
            .assertTrue("Expired event is not opened.");

        CalendarExpiredEventsPageActions.hideExpiredEvent(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarExpiredEventsPageActions.getEvents(driver).isEmpty())
            .assertTrue("Expired event is not hidden.");
    }

    @Test(groups = {"fe", "calendar"})
    void extendExpiredEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);
        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.CALENDAR);
        CommonUtils.enableTestMode(driver);

        createExpiredEvent(driver);

        CalendarIndexPageActions.toExpiredEventsPage(driver);

        AwaitilityWrapper.getListWithWait(() -> CalendarExpiredEventsPageActions.getEvents(driver), webElements -> !webElements.isEmpty())
            .getFirst()
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(driver, By.id("calendar-opened-event")).isPresent())
            .assertTrue("Expired event is not opened.");

        emptyExtendUntil(driver);

        CalendarExpiredEventsPageActions.setExtendUntil(driver, CURRENT_DATE.plusWeeks(2));
        CalendarExpiredEventsPageActions.extendExpiredEvent(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarExpiredEventsPageActions.getEvents(driver).isEmpty())
            .assertTrue("Expired event is not hidden.");
    }


    private static void emptyExtendUntil(WebDriver driver) {
        CalendarExpiredEventsPageActions.extendExpiredEvent(driver);
        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.CALENDAR_EMPTY_EXTEND_UNTIL_DATE);
    }

    private static void createExpiredEvent(WebDriver driver) {
        CalendarIndexPageActions.openCreateEventPage(driver);
        CreateEventParameters event = CreateEventParameters.valid(RepetitionType.DAYS_OF_WEEK)
            .toBuilder()
            .repetitionData(List.of(CURRENT_DATE.getDayOfWeek()))
            .startDate(START_DATE)
            .endDate(END_DATE)
            .build();
        CalendarEventPageActions.fillForm(driver, event);
        CalendarEventPageActions.create(driver);
        ToastMessageUtil.verifySuccessToast(driver, LocalizedText.CALENDAR_EVENT_CREATED);
    }
}
