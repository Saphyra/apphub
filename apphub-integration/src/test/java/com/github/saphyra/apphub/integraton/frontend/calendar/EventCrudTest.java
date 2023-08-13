package com.github.saphyra.apphub.integraton.frontend.calendar;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.calendar.CalendarActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.calendar.RepetitionType;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class EventCrudTest extends SeleniumTest {
    private static final LocalDate CURRENT_DATE = LocalDate.now();
    private static final LocalDate FIRST_OF_MONTH = LocalDate.of(CURRENT_DATE.getYear(), CURRENT_DATE.getMonth(), 1);
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String NEW_TITLE = "new-title";
    private static final String NOTE = "note";
    private static final String NEW_CONTENT = "new-content";

    @Test
    public void oneTimeEvent_changeStatuses() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.CALENDAR);

        CalendarActions.openCreateEventWindowAt(driver, CURRENT_DATE);

        //Create - Empty title
        CalendarActions.fillEventTitle(driver, " ");

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Title must not be empty.");

        //Create - No Hours
        CalendarActions.fillEventTitle(driver, TITLE);
        CalendarActions.fillEventContent(driver, CONTENT);

        CalendarActions.setCreateEventHours(driver, "");
        CalendarActions.setCreateEventMinutes(driver, "10");

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Both or neither hours and minutes need to be filled.");

        //Create - No Minutes
        CalendarActions.setCreateEventMinutes(driver, "");
        CalendarActions.setCreateEventHours(driver, "10");

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Both or neither hours and minutes need to be filled.");

        //Create
        CalendarActions.setCreateEventHours(driver, "");

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Event created.");

        WebElement dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(dailyTask.getText()).isEqualTo(TITLE);
        assertThat(dailyTask.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase());

        WebElement calendarEvent = CalendarActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(calendarEvent.getText()).isEqualTo(TITLE);
        assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase());

        //View event
        CalendarActions.openEvent(driver, dailyTask);

        CalendarActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.CALENDAR_OCCURRENCE_STATUS_PENDING, false);

        //Mark as done
        CalendarActions.markAsDone(driver);

        SleepUtil.sleep(1000);
        CalendarActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_DONE.toLowerCase());

        calendarEvent = CalendarActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_DONE.toLowerCase());

        dailyTask.click();

        CalendarActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.CALENDAR_OCCURRENCE_STATUS_DONE, false);

        //Mark as undone
        CalendarActions.markAsNotDone(driver);

        SleepUtil.sleep(1000);
        CalendarActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase());

        calendarEvent = CalendarActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase());

        dailyTask.click();

        CalendarActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.CALENDAR_OCCURRENCE_STATUS_PENDING, false);

        //Mark as snoozed
        CalendarActions.markAsSnoozed(driver);

        SleepUtil.sleep(1000);
        CalendarActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_SNOOZED.toLowerCase());

        calendarEvent = CalendarActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_SNOOZED.toLowerCase());

        dailyTask.click();

        CalendarActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.CALENDAR_OCCURRENCE_STATUS_SNOOZED, false);

        //Mark as unsnoozed
        CalendarActions.markAsUnsnoozed(driver);

        SleepUtil.sleep(1000);
        CalendarActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase());

        calendarEvent = CalendarActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase());

        dailyTask.click();

        CalendarActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.CALENDAR_OCCURRENCE_STATUS_PENDING, false);

        //Delete
        CalendarActions.deleteEvent(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarActions.getDailyTasks(driver).isEmpty())
            .assertTrue("Event is not deleted");

        assertThat(CalendarActions.getEventsOfDay(driver, CURRENT_DATE)).isEmpty();
    }

    @Test
    public void daysOfWeekEvent_editEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.CALENDAR);

        CalendarActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH);

        //Create - No days set
        CalendarActions.fillEventTitle(driver, TITLE);
        CalendarActions.fillEventContent(driver, CONTENT);
        CalendarActions.setCreateEventRepetitionType(driver, RepetitionType.DAYS_OF_WEEK);

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "No day selected.");

        //Create
        CalendarActions.selectDayOfWeek(driver, FIRST_OF_MONTH.getDayOfWeek());

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Event created.");

        WebElement dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(dailyTask.getText()).isEqualTo(TITLE);
        assertThat(dailyTask.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(dailyTask)).containsAnyElementsOf(getStatusOfDay(FIRST_OF_MONTH));

        for (int i = 0; i <= 4; i++) {
            LocalDate date = FIRST_OF_MONTH.plusWeeks(i);

            WebElement calendarEvent = CalendarActions.getEventsOfDay(driver, date)
                .get(0);
            assertThat(calendarEvent.getText()).isEqualTo(TITLE);
            assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
            assertThat(WebElementUtils.getClasses(calendarEvent)).containsAnyElementsOf(getStatusOfDay(date));
        }

        //Edit event - Empty Title
        CalendarActions.openEvent(driver, dailyTask);

        CalendarActions.enableEditing(driver);

        CalendarActions.verifyViewEventPage(
            driver,
            TITLE,
            CONTENT,
            "",
            ((FIRST_OF_MONTH.isBefore(CURRENT_DATE) ? Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED : Constants.CALENDAR_OCCURRENCE_STATUS_PENDING)),
            true
        );

        CalendarActions.editTitle(driver, "");

        CalendarActions.saveModifications(driver);

        NotificationUtil.verifyErrorNotification(driver, "Title must not be empty.");

        //Edit event
        CalendarActions.editTitle(driver, NEW_TITLE);
        CalendarActions.editContent(driver, NEW_CONTENT);
        CalendarActions.editNote(driver, NOTE);

        CalendarActions.saveModifications(driver);

        CalendarActions.verifyViewEventPage(
            driver,
            NEW_TITLE,
            NEW_CONTENT,
            NOTE,
            ((FIRST_OF_MONTH.isBefore(CURRENT_DATE) ? Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED : Constants.CALENDAR_OCCURRENCE_STATUS_PENDING)),
            false
        );

        CalendarActions.closeViewEventPage(driver);

        for (int i = 0; i <= 4; i++) {
            LocalDate date = FIRST_OF_MONTH.plusWeeks(i);

            WebElement calendarEvent = CalendarActions.getEventsOfDay(driver, date)
                .get(0);
            assertThat(calendarEvent.getText()).isEqualTo(NEW_TITLE);
            assertThat(calendarEvent.getAttribute("title")).contains(NEW_CONTENT);
        }
    }

    @Test
    public void daysOfMonthEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.CALENDAR);

        CalendarActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH);

        //Create - No days set
        CalendarActions.fillEventTitle(driver, TITLE);
        CalendarActions.fillEventContent(driver, CONTENT);
        CalendarActions.setCreateEventRepetitionType(driver, RepetitionType.DAYS_OF_MONTH);

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "No day selected.");

        //Create
        CalendarActions.selectDayOfMonth(driver, 21);

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Event created.");

        WebElement dailyTask = AwaitilityWrapper.getListWithWait(() -> CalendarActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(dailyTask.getText()).isEqualTo(TITLE);
        assertThat(dailyTask.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(dailyTask)).containsAnyElementsOf(getStatusOfDay(FIRST_OF_MONTH));

        CalendarActions.nextMonth(driver);

        LocalDate date = LocalDate.of(FIRST_OF_MONTH.getYear(), FIRST_OF_MONTH.getMonth(), 21)
            .plusMonths(1);

        WebElement calendarEvent = CalendarActions.getEventsOfDay(driver, date)
            .get(0);
        assertThat(calendarEvent.getText()).isEqualTo(TITLE);
        assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(calendarEvent)).containsAnyElementsOf(getStatusOfDay(date));
    }

    @Test
    public void everyXDaysEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.CALENDAR);

        CalendarActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH);

        //Create - No days set
        CalendarActions.fillEventTitle(driver, TITLE);
        CalendarActions.fillEventContent(driver, CONTENT);
        CalendarActions.setCreateEventRepetitionType(driver, RepetitionType.EVERY_X_DAYS);

        CalendarActions.fillRepetitionDays(driver, 0);

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Days too low (minimum 1).");

        //Create
        CalendarActions.fillRepetitionDays(driver, 5);

        CalendarActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Event created.");

        for (int i = 0; i <= 25; i += 5) {
            LocalDate date = FIRST_OF_MONTH.plusDays(i);

            WebElement calendarEvent = CalendarActions.getEventsOfDay(driver, date)
                .get(0);
            assertThat(calendarEvent.getText()).isEqualTo(TITLE);
            assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
            assertThat(WebElementUtils.getClasses(calendarEvent)).containsAnyElementsOf(getStatusOfDay(date));
        }
    }

    private List<String> getStatusOfDay(LocalDate date) {
        if (date.isBefore(CURRENT_DATE)) {
            return List.of(Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED.toLowerCase());
        }

        return List.of(
            Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED.toLowerCase(),
            Constants.CALENDAR_OCCURRENCE_STATUS_PENDING.toLowerCase(),
            Constants.CALENDAR_OCCURRENCE_STATUS_VIRTUAL.toLowerCase()
        );
    }
}
