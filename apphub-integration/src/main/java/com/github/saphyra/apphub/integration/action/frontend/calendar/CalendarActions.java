package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.calendar.CalendarSearchResult;
import com.github.saphyra.apphub.integration.structure.calendar.RepetitionType;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class CalendarActions {
    public static void createEvent(WebDriver driver, LocalDate date, String title) {
        openCreateEventWindowAt(driver, date);
        fillEventTitle(driver, title);
        pushCreateEventButton(driver);

        NotificationUtil.verifySuccessNotification(driver, "Esemény létrehozva.");
    }

    public static void selectDay(WebDriver driver, LocalDate currentDate) {
        AwaitilityWrapper.getWithWait(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-day-" + currentDate))), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException(currentDate + " is not opened."))
            .get()
            .findElement(By.cssSelector(":scope .calendar-day-title"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarPage.dailyTasksCurrentDay(driver).getText().split(" ")[2].equals(currentDate.format(DateTimeFormatter.ofPattern("dd"))))
            .assertTrue("Day is not opened.");
    }

    public static void openCreateEventWindowAt(WebDriver driver, LocalDate currentDate) {
        selectDay(driver, currentDate);

        CalendarPage.createTaskButton(driver)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarPage.createEventPage(driver).isDisplayed())
            .assertTrue("CreateEvent window is not displayed");
    }

    public static void pushCreateEventButton(WebDriver driver) {
        CalendarPage.createEventButton(driver)
            .click();
    }

    public static void fillEventTitle(WebDriver driver, String title) {
        clearAndFill(CalendarPage.createEventTitleInput(driver), title);
    }

    public static void fillEventContent(WebDriver driver, String content) {
        clearAndFill(CalendarPage.createEventContentInput(driver), content);
    }

    public static List<WebElement> getDailyTasks(WebDriver driver) {
        return CalendarPage.dailyTasks(driver);
    }

    public static List<WebElement> getEventsOfDay(WebDriver driver, LocalDate date) {
        return AwaitilityWrapper.getWithWait(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-day-" + date))), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("No day found for date " + date))
            .get()
            .findElements(By.cssSelector(":scope .calendar-event"));
    }

    public static void openEvent(WebDriver driver, String title) {
        WebElement dailyTask = AwaitilityWrapper.getWithWait(
            () -> getDailyTasks(driver)
                .stream()
                .filter(webElement -> webElement.getText().equals(title))
                .findAny(),
            Optional::isPresent
        )
            .orElseThrow(() -> new RuntimeException("No daily task found with title " + title))
            .orElseThrow(() -> new RuntimeException("No daily task found with title " + title));

        openEvent(driver, dailyTask);
    }

    public static void openEvent(WebDriver driver, WebElement dailyTask) {
        dailyTask.click();

        AwaitilityWrapper.createDefault()
            .until(() -> CalendarPage.viewEventPage(driver).isDisplayed())
            .assertTrue("ViewEvent page is not displayed.");
    }

    public static void verifyViewEventPage(WebDriver driver, String title, String content, String note, String status, boolean editingEnabled) {
        assertThat(CalendarPage.viewEventTitle(driver).getText()).isEqualTo(title);
        assertThat(CalendarPage.viewEventContent(driver).getAttribute("value")).isEqualTo(content);
        assertThat(CalendarPage.viewEventNote(driver).getAttribute("value")).isEqualTo(note);

        assertThat(CalendarPage.viewEventDeleteButton(driver).isDisplayed()).isTrue();

        assertThat(CalendarPage.viewEventTitle(driver).getAttribute("contenteditable")).isEqualTo(String.valueOf(editingEnabled));
        assertThat(Optional.ofNullable(CalendarPage.viewEventContent(driver).getAttribute("disabled")).orElse("false")).isEqualTo(String.valueOf(!editingEnabled));
        assertThat(Optional.ofNullable(CalendarPage.viewEventNote(driver).getAttribute("disabled")).orElse("false")).isEqualTo(String.valueOf(!editingEnabled));

        if (editingEnabled) {
            assertThat(CalendarPage.viewEventEditButton(driver).isDisplayed()).isFalse();
            assertThat(CalendarPage.viewEventSaveButton(driver).isDisplayed()).isTrue();
            assertThat(CalendarPage.viewEventDiscardButton(driver).isDisplayed()).isTrue();
        } else {
            assertThat(CalendarPage.viewEventEditButton(driver).isDisplayed()).isTrue();
            assertThat(CalendarPage.viewEventSaveButton(driver).isDisplayed()).isFalse();
            assertThat(CalendarPage.viewEventDiscardButton(driver).isDisplayed()).isFalse();
        }

        switch (status) {
            case Constants.CALENDAR_OCCURRENCE_STATUS_PENDING:
            case Constants.CALENDAR_OCCURRENCE_STATUS_VIRTUAL:
            case Constants.CALENDAR_OCCURRENCE_STATUS_EXPIRED:
                assertThat(CalendarPage.viewEventDoneButton(driver).isDisplayed()).isTrue();
                assertThat(CalendarPage.viewEventSnoozeButton(driver).isDisplayed()).isTrue();
                assertThat(CalendarPage.viewEventUnsnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(CalendarPage.viewEventNotDoneButton(driver).isDisplayed()).isFalse();
                break;
            case Constants.CALENDAR_OCCURRENCE_STATUS_DONE:
                assertThat(CalendarPage.viewEventDoneButton(driver).isDisplayed()).isFalse();
                assertThat(CalendarPage.viewEventSnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(CalendarPage.viewEventUnsnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(CalendarPage.viewEventNotDoneButton(driver).isDisplayed()).isTrue();
                break;
            case Constants.CALENDAR_OCCURRENCE_STATUS_SNOOZED:
                assertThat(CalendarPage.viewEventDoneButton(driver).isDisplayed()).isTrue();
                assertThat(CalendarPage.viewEventSnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(CalendarPage.viewEventUnsnoozeButton(driver).isDisplayed()).isTrue();
                assertThat(CalendarPage.viewEventNotDoneButton(driver).isDisplayed()).isFalse();
                break;
            default:
                throw new RuntimeException("Unhandled status: " + status);
        }
    }

    public static void markAsDone(WebDriver driver) {
        CalendarPage.viewEventDoneButton(driver)
            .click();
    }

    public static void closeViewEventPage(WebDriver driver) {
        NotificationUtil.clearNotifications(driver);

        CalendarPage.viewEventWindowCloseButton(driver)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> !CalendarPage.viewEventPage(driver).isDisplayed())
            .assertTrue("ViewEvent page is not closed.");
    }

    public static void markAsNotDone(WebDriver driver) {
        CalendarPage.viewEventNotDoneButton(driver)
            .click();
    }

    public static void markAsSnoozed(WebDriver driver) {
        CalendarPage.viewEventSnoozeButton(driver)
            .click();
    }

    public static void markAsUnsnoozed(WebDriver driver) {
        CalendarPage.viewEventUnsnoozeButton(driver)
            .click();
    }

    public static void deleteEvent(WebDriver driver) {
        CalendarPage.viewEventDeleteButton(driver)
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "delete-event-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver, "Esemény törölve.");
    }

    public static void setCreateEventRepetitionType(WebDriver driver, RepetitionType repetitionType) {
        WebElementUtils.selectOption(CalendarPage.createEventRepetitionTypeSelect(driver), repetitionType.name());
    }

    public static void selectDayOfWeek(WebDriver driver, DayOfWeek dayOfWeek) {
        CalendarPage.createEventDaysOfWeekInputs(driver)
            .stream()
            .filter(webElement -> webElement.getAttribute("value").equals(dayOfWeek.name()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(dayOfWeek + " not found."))
            .click();
    }

    public static void enableEditing(WebDriver driver) {
        CalendarPage.viewEventEditButton(driver)
            .click();
    }

    public static void editTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFillContentEditable(driver, CalendarPage.viewEventTitle(driver), title);
    }

    public static void saveModifications(WebDriver driver) {
        CalendarPage.viewEventSaveButton(driver)
            .click();
    }

    public static void editNote(WebDriver driver, String note) {
        clearAndFill(CalendarPage.viewEventNote(driver), note);
    }

    public static void editContent(WebDriver driver, String newContent) {
        clearAndFill(CalendarPage.viewEventContent(driver), newContent);
    }

    public static void fillRepetitionDays(WebDriver driver, int repetitionDays) {
        clearAndFill(CalendarPage.createEventRepetitionTypeDaysInput(driver), String.valueOf(repetitionDays));
    }

    public static void previousMonth(WebDriver driver) {
        CalendarPage.previousMonthButton(driver)
            .click();
    }

    public static void nextMonth(WebDriver driver) {
        CalendarPage.nextMonthButton(driver)
            .click();
    }

    public static void selectDayOfMonth(WebDriver driver, int dayOfMonth) {
        clearAndFill(CalendarPage.createEventRepetitionTypeDaysOfMonthInput(driver), dayOfMonth);

        CalendarPage.createEventDaysOfMonthAddDayButton(driver)
            .click();
    }

    public static void setCreateEventMinutes(WebDriver driver, String minutes) {
        WebElementUtils.selectOption(CalendarPage.createEventMinutes(driver), minutes);
    }

    public static void setCreateEventHours(WebDriver driver, String hours) {
        WebElementUtils.selectOption(CalendarPage.createEventHours(driver), hours);
    }

    public static void searchInFooter(WebDriver driver, String query) {
        WebElementUtils.clearAndFill(CalendarPage.searchInFooterInput(driver), query);

        CalendarPage.searchInFooterButton(driver)
            .click();
    }

    public static List<CalendarSearchResult> searchResult(WebDriver driver) {
        return CalendarPage.searchResult(driver)
            .stream()
            .map(CalendarSearchResult::new)
            .collect(Collectors.toList());
    }

    public static void searchInResult(WebDriver driver, String query) {
        WebElementUtils.clearAndFill(CalendarPage.searchInResultInput(driver), query);

        CalendarPage.searchInResultButton(driver)
            .click();
    }
}
