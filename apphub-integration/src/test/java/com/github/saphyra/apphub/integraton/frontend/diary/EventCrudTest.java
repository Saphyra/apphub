package com.github.saphyra.apphub.integraton.frontend.diary;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.diary.DiaryActions;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.diary.RepetitionType;
import com.github.saphyra.apphub.integration.structure.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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

    @Test(groups = "diary")
    public void oneTimeEvent_changeStatuses() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.DIARY);

        DiaryActions.openCreateEventWindowAt(driver, CURRENT_DATE);

        //Create - Empty title
        DiaryActions.fillEventTitle(driver, " ");

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Create - No Hours
        DiaryActions.fillEventTitle(driver, TITLE);
        DiaryActions.fillEventContent(driver, CONTENT);

        DiaryActions.setCreateEventHours(driver, "");
        DiaryActions.setCreateEventMinutes(driver, "10");

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Az órát és a percet is ki kell tölteni, vagy egyiket sem.");

        //Create - No Minutes
        DiaryActions.setCreateEventMinutes(driver, "");
        DiaryActions.setCreateEventHours(driver, "10");

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Az órát és a percet is ki kell tölteni, vagy egyiket sem.");

        //Create
        DiaryActions.setCreateEventHours(driver, "");

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Esemény létrehozva.");

        WebElement dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(dailyTask.getText()).isEqualTo(TITLE);
        assertThat(dailyTask.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase());

        WebElement calendarEvent = DiaryActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(calendarEvent.getText()).isEqualTo(TITLE);
        assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase());

        //View event
        DiaryActions.openEvent(driver, dailyTask);

        DiaryActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.DIARY_OCCURRENCE_STATUS_PENDING, false);

        //Mark as done
        DiaryActions.markAsDone(driver);

        SleepUtil.sleep(1000);
        DiaryActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.DIARY_OCCURRENCE_STATUS_DONE.toLowerCase());

        calendarEvent = DiaryActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.DIARY_OCCURRENCE_STATUS_DONE.toLowerCase());

        dailyTask.click();

        DiaryActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.DIARY_OCCURRENCE_STATUS_DONE, false);

        //Mark as undone
        DiaryActions.markAsNotDone(driver);

        SleepUtil.sleep(1000);
        DiaryActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase());

        calendarEvent = DiaryActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase());

        dailyTask.click();

        DiaryActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.DIARY_OCCURRENCE_STATUS_PENDING, false);

        //Mark as snoozed
        DiaryActions.markAsSnoozed(driver);

        SleepUtil.sleep(1000);
        DiaryActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.DIARY_OCCURRENCE_STATUS_SNOOZED.toLowerCase());

        calendarEvent = DiaryActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.DIARY_OCCURRENCE_STATUS_SNOOZED.toLowerCase());

        dailyTask.click();

        DiaryActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.DIARY_OCCURRENCE_STATUS_SNOOZED, false);

        //Mark as unsnoozed
        DiaryActions.markAsUnsnoozed(driver);

        SleepUtil.sleep(1000);
        DiaryActions.closeViewEventPage(driver);

        dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(WebElementUtils.getClasses(dailyTask)).contains(Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase());

        calendarEvent = DiaryActions.getEventsOfDay(driver, CURRENT_DATE)
            .get(0);
        assertThat(WebElementUtils.getClasses(calendarEvent)).contains(Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase());

        dailyTask.click();

        DiaryActions.verifyViewEventPage(driver, TITLE, CONTENT, "", Constants.DIARY_OCCURRENCE_STATUS_PENDING, false);

        //Delete
        DiaryActions.deleteEvent(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> DiaryActions.getDailyTasks(driver).isEmpty())
            .assertTrue("Event is not deleted");

        assertThat(DiaryActions.getEventsOfDay(driver, CURRENT_DATE)).isEmpty();
    }

    @Test(groups = "diary")
    public void daysOfWeekEvent_editEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.DIARY);

        DiaryActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH);

        //Create - No days set
        DiaryActions.fillEventTitle(driver, TITLE);
        DiaryActions.fillEventContent(driver, CONTENT);
        DiaryActions.setCreateEventRepetitionType(driver, RepetitionType.DAYS_OF_WEEK);

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Nincs nap kiválasztva.");

        //Create
        DiaryActions.selectDayOfWeek(driver, FIRST_OF_MONTH.getDayOfWeek());

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Esemény létrehozva.");

        WebElement dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(dailyTask.getText()).isEqualTo(TITLE);
        assertThat(dailyTask.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(dailyTask)).containsAnyElementsOf(getStatusOfDay(FIRST_OF_MONTH));

        for (int i = 0; i <= 4; i++) {
            LocalDate date = FIRST_OF_MONTH.plusWeeks(i);

            WebElement calendarEvent = DiaryActions.getEventsOfDay(driver, date)
                .get(0);
            assertThat(calendarEvent.getText()).isEqualTo(TITLE);
            assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
            assertThat(WebElementUtils.getClasses(calendarEvent)).containsAnyElementsOf(getStatusOfDay(date));
        }

        //Edit event - Empty Title
        DiaryActions.openEvent(driver, dailyTask);

        DiaryActions.enableEditing(driver);

        DiaryActions.verifyViewEventPage(
            driver,
            TITLE,
            CONTENT,
            "",
            ((FIRST_OF_MONTH.isBefore(CURRENT_DATE) ? Constants.DIARY_OCCURRENCE_STATUS_EXPIRED : Constants.DIARY_OCCURRENCE_STATUS_PENDING)),
            true
        );

        DiaryActions.editTitle(driver, "");

        DiaryActions.saveModifications(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Edit event
        DiaryActions.editTitle(driver, NEW_TITLE);
        DiaryActions.editContent(driver, NEW_CONTENT);
        DiaryActions.editNote(driver, NOTE);

        DiaryActions.saveModifications(driver);

        DiaryActions.verifyViewEventPage(
            driver,
            NEW_TITLE,
            NEW_CONTENT,
            NOTE,
            ((FIRST_OF_MONTH.isBefore(CURRENT_DATE) ? Constants.DIARY_OCCURRENCE_STATUS_EXPIRED : Constants.DIARY_OCCURRENCE_STATUS_PENDING)),
            false
        );

        DiaryActions.closeViewEventPage(driver);

        for (int i = 0; i <= 4; i++) {
            LocalDate date = FIRST_OF_MONTH.plusWeeks(i);

            WebElement calendarEvent = DiaryActions.getEventsOfDay(driver, date)
                .get(0);
            assertThat(calendarEvent.getText()).isEqualTo(NEW_TITLE);
            assertThat(calendarEvent.getAttribute("title")).contains(NEW_CONTENT);
        }
    }

    @Test(groups = "diary")
    public void daysOfMonthEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.DIARY);

        DiaryActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH);

        //Create - No days set
        DiaryActions.fillEventTitle(driver, TITLE);
        DiaryActions.fillEventContent(driver, CONTENT);
        DiaryActions.setCreateEventRepetitionType(driver, RepetitionType.DAYS_OF_MONTH);

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Nincs nap kiválasztva.");

        //Create
        DiaryActions.selectDayOfMonth(driver, 21);

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Esemény létrehozva.");

        WebElement dailyTask = AwaitilityWrapper.getListWithWait(() -> DiaryActions.getDailyTasks(driver), ts -> !ts.isEmpty())
            .get(0);
        assertThat(dailyTask.getText()).isEqualTo(TITLE);
        assertThat(dailyTask.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(dailyTask)).containsAnyElementsOf(getStatusOfDay(FIRST_OF_MONTH));

        DiaryActions.nextMonth(driver);

        LocalDate date = LocalDate.of(FIRST_OF_MONTH.getYear(), FIRST_OF_MONTH.getMonth(), 21)
            .plusMonths(1);

        WebElement calendarEvent = DiaryActions.getEventsOfDay(driver, date)
            .get(0);
        assertThat(calendarEvent.getText()).isEqualTo(TITLE);
        assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
        assertThat(WebElementUtils.getClasses(calendarEvent)).containsAnyElementsOf(getStatusOfDay(date));
    }

    @Test(groups = "diary")
    public void everyXDaysEvent() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);

        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.DIARY);

        DiaryActions.openCreateEventWindowAt(driver, FIRST_OF_MONTH);

        //Create - No days set
        DiaryActions.fillEventTitle(driver, TITLE);
        DiaryActions.fillEventContent(driver, CONTENT);
        DiaryActions.setCreateEventRepetitionType(driver, RepetitionType.EVERY_X_DAYS);

        DiaryActions.fillRepetitionDays(driver, 0);

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifyErrorNotification(driver, "Napok száma túl alacsony (minimum 1)");

        //Create
        DiaryActions.fillRepetitionDays(driver, 5);

        DiaryActions.pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Esemény létrehozva.");

        for (int i = 0; i <= 25; i += 5) {
            LocalDate date = FIRST_OF_MONTH.plusDays(i);

            WebElement calendarEvent = DiaryActions.getEventsOfDay(driver, date)
                .get(0);
            assertThat(calendarEvent.getText()).isEqualTo(TITLE);
            assertThat(calendarEvent.getAttribute("title")).endsWith(CONTENT);
            assertThat(WebElementUtils.getClasses(calendarEvent)).containsAnyElementsOf(getStatusOfDay(date));
        }
    }

    private List<String> getStatusOfDay(LocalDate date) {
        if (date.isBefore(CURRENT_DATE)) {
            return List.of(Constants.DIARY_OCCURRENCE_STATUS_EXPIRED.toLowerCase());
        }

        return List.of(
            Constants.DIARY_OCCURRENCE_STATUS_EXPIRED.toLowerCase(),
            Constants.DIARY_OCCURRENCE_STATUS_PENDING.toLowerCase(),
            Constants.DIARY_OCCURRENCE_STATUS_VIRTUAL.toLowerCase()
        );
    }
}
