package com.github.saphyra.apphub.integraton.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class SortEventsTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate FIRST_OF_MONTH = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), 1);
    private static final String EXPIRED_TITLE = "expired-title";
    private static final String SNOOZED_TITLE = "snoozed-title";
    private static final String PENDING_TITLE = "pending-title";
    private static final String DONE_TITLE = "done-title";
    private static final String LATER_PENDING_TITLE = "later-pending-title";
    private static final String NO_TIME_PENDING_TITLE = "no-time-pending-title";

    @Test(groups = {"fe", "calendar"})
    public void orderOfEvents() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.CALENDAR);

        createExpiredEvent(driver);
        createSnoozedEvent(driver);
        createDoneEvent(driver);
        createPendingEvent(driver);
        checkOrder(driver);
    }

    private static void createExpiredEvent(WebDriver driver) {
        CalendarActions.previousMonth(driver);
        CalendarActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH.minusMonths(1));
        CalendarActions.fillEventTitle(driver, EXPIRED_TITLE);
        CalendarActions.pushCreateEventButton(driver);

        SleepUtil.sleep(1000);
    }

    private static void createSnoozedEvent(WebDriver driver) {
        CalendarActions.nextMonth(driver);
        CalendarActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        CalendarActions.fillEventTitle(driver, SNOOZED_TITLE);
        CalendarActions.pushCreateEventButton(driver);
        CalendarActions.openEvent(driver, SNOOZED_TITLE);
        CalendarActions.markAsSnoozed(driver);
        CalendarActions.closeViewEventPage(driver);
    }

    private static void createDoneEvent(WebDriver driver) {
        CalendarActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        CalendarActions.fillEventTitle(driver, DONE_TITLE);
        CalendarActions.pushCreateEventButton(driver);
        CalendarActions.openEvent(driver, DONE_TITLE);
        CalendarActions.markAsDone(driver);
        CalendarActions.closeViewEventPage(driver);
    }

    private static void createPendingEvent(WebDriver driver) {
        CalendarActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        CalendarActions.fillEventTitle(driver, PENDING_TITLE);
        CalendarActions.setCreateEventHours(driver, "10");
        CalendarActions.setCreateEventMinutes(driver, "10");
        CalendarActions.pushCreateEventButton(driver);

        CalendarActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        CalendarActions.fillEventTitle(driver, LATER_PENDING_TITLE);
        CalendarActions.setCreateEventHours(driver, "10");
        CalendarActions.setCreateEventMinutes(driver, "11");
        CalendarActions.pushCreateEventButton(driver);

        CalendarActions.openCreateEventWindowAt(driver, CURRENT_DATE);
        CalendarActions.fillEventTitle(driver, NO_TIME_PENDING_TITLE);
        CalendarActions.pushCreateEventButton(driver);

        SleepUtil.sleep(1000);
    }

    private static void checkOrder(WebDriver driver) {
        List<String> dailyTasks = CalendarActions.getDailyTasks(driver)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
        assertThat(dailyTasks).containsExactly(EXPIRED_TITLE, PENDING_TITLE, LATER_PENDING_TITLE, NO_TIME_PENDING_TITLE, DONE_TITLE, SNOOZED_TITLE);

        List<String> calendarDayTasks = CalendarActions.getEventsOfDay(driver, CURRENT_DATE)
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
        assertThat(calendarDayTasks).containsExactly(EXPIRED_TITLE, PENDING_TITLE, LATER_PENDING_TITLE, NO_TIME_PENDING_TITLE, DONE_TITLE, SNOOZED_TITLE);
    }
}
