package com.github.saphyra.apphub.integration.action.frontend.diary;

import com.github.saphyra.apphub.integration.action.frontend.common.CommonPageActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.diary.RepetitionType;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;
import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class DiaryActions {
    public static void selectDay(WebDriver driver, LocalDate currentDate) {
        driver.findElement(By.id("calendar-day-" + currentDate))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> DiaryPage.dailyTasksCurrentDay(driver).getText().split(" ")[2].equals(currentDate.format(DateTimeFormatter.ofPattern("dd"))))
            .assertTrue("Day is not opened.");
    }

    public static void openCreateEventWindowAt(WebDriver driver, LocalDate currentDate) {
        selectDay(driver, currentDate);

        DiaryPage.createTaskButton(driver)
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> DiaryPage.createEventPage(driver).isDisplayed())
            .assertTrue("CreateEvent window is not displayed");
    }

    public static void createEvent(WebDriver driver) {
        DiaryPage.createEventButton(driver)
            .click();
    }

    public static void fillEventTitle(WebDriver driver, String title) {
        clearAndFill(DiaryPage.createEventTitleInput(driver), title);
    }

    public static void fillEventContent(WebDriver driver, String content) {
        clearAndFill(DiaryPage.createEventContentInput(driver), content);
    }

    public static List<WebElement> getDailyTasks(WebDriver driver) {
        return DiaryPage.dailyTasks(driver);
    }

    public static List<WebElement> getEventsOfDay(WebDriver driver, LocalDate currentDate) {
        return driver.findElement(By.id("calendar-day-" + currentDate))
            .findElements(By.cssSelector(":scope .calendar-event"));
    }

    public static void openEvent(WebDriver driver, WebElement dailyTask) {
        dailyTask.click();

        AwaitilityWrapper.createDefault()
            .until(() -> DiaryPage.viewEventPage(driver).isDisplayed())
            .assertTrue("ViewEvent page is not displayed.");
    }

    public static void verifyViewEventPage(WebDriver driver, String title, String content, String note, String status, boolean editingEnabled) {
        assertThat(DiaryPage.viewEventTitle(driver).getText()).isEqualTo(title);
        assertThat(DiaryPage.viewEventContent(driver).getAttribute("value")).isEqualTo(content);
        assertThat(DiaryPage.viewEventNote(driver).getAttribute("value")).isEqualTo(note);

        assertThat(DiaryPage.viewEventDeleteButton(driver).isDisplayed()).isTrue();

        assertThat(DiaryPage.viewEventTitle(driver).getAttribute("contenteditable")).isEqualTo(String.valueOf(editingEnabled));
        assertThat(Optional.ofNullable(DiaryPage.viewEventContent(driver).getAttribute("disabled")).orElse("false")).isEqualTo(String.valueOf(!editingEnabled));
        assertThat(Optional.ofNullable(DiaryPage.viewEventNote(driver).getAttribute("disabled")).orElse("false")).isEqualTo(String.valueOf(!editingEnabled));

        if (editingEnabled) {
            assertThat(DiaryPage.viewEventEditButton(driver).isDisplayed()).isFalse();
            assertThat(DiaryPage.viewEventSaveButton(driver).isDisplayed()).isTrue();
            assertThat(DiaryPage.viewEventDiscardButton(driver).isDisplayed()).isTrue();
        } else {
            assertThat(DiaryPage.viewEventEditButton(driver).isDisplayed()).isTrue();
            assertThat(DiaryPage.viewEventSaveButton(driver).isDisplayed()).isFalse();
            assertThat(DiaryPage.viewEventDiscardButton(driver).isDisplayed()).isFalse();
        }

        switch (status) {
            case Constants.DIARY_OCCURRENCE_STATUS_PENDING:
            case Constants.DIARY_OCCURRENCE_STATUS_VIRTUAL:
            case Constants.DIARY_OCCURRENCE_STATUS_EXPIRED:
                assertThat(DiaryPage.viewEventDoneButton(driver).isDisplayed()).isTrue();
                assertThat(DiaryPage.viewEventSnoozeButton(driver).isDisplayed()).isTrue();
                assertThat(DiaryPage.viewEventUnsnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(DiaryPage.viewEventNotDoneButton(driver).isDisplayed()).isFalse();
                break;
            case Constants.DIARY_OCCURRENCE_STATUS_DONE:
                assertThat(DiaryPage.viewEventDoneButton(driver).isDisplayed()).isFalse();
                assertThat(DiaryPage.viewEventSnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(DiaryPage.viewEventUnsnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(DiaryPage.viewEventNotDoneButton(driver).isDisplayed()).isTrue();
                break;
            case Constants.DIARY_OCCURRENCE_STATUS_SNOOZED:
                assertThat(DiaryPage.viewEventDoneButton(driver).isDisplayed()).isTrue();
                assertThat(DiaryPage.viewEventSnoozeButton(driver).isDisplayed()).isFalse();
                assertThat(DiaryPage.viewEventUnsnoozeButton(driver).isDisplayed()).isTrue();
                assertThat(DiaryPage.viewEventNotDoneButton(driver).isDisplayed()).isFalse();
                break;
            default:
                throw new RuntimeException("Unhandled status: " + status);
        }
    }

    public static void markAsDone(WebDriver driver) {
        DiaryPage.viewEventDoneButton(driver)
            .click();
    }

    public static void closeViewEventPage(WebDriver driver) {
        DiaryPage.viewEventWindowCloseButton(driver)
            .click();
    }

    public static void markAsNotDone(WebDriver driver) {
        DiaryPage.viewEventNotDoneButton(driver)
            .click();
    }

    public static void markAsSnoozed(WebDriver driver) {
        DiaryPage.viewEventSnoozeButton(driver)
            .click();
    }

    public static void markAsUnsnoozed(WebDriver driver) {
        DiaryPage.viewEventUnsnoozeButton(driver)
            .click();
    }

    public static void deleteEvent(WebDriver driver) {
        DiaryPage.viewEventDeleteButton(driver)
            .click();

        CommonPageActions.confirmConfirmationDialog(driver, "delete-event-confirmation-dialog");

        NotificationUtil.verifySuccessNotification(driver, "Esemény törölve.");
    }

    public static void setCreateEventRepetitionType(WebDriver driver, RepetitionType repetitionType) {
        WebElementUtils.selectOption(DiaryPage.createEventRepetitionTypeSelect(driver), repetitionType.name());
    }

    public static void selectDayOfWeek(WebDriver driver, DayOfWeek dayOfWeek) {
        DiaryPage.createEventDaysOfWeekInputs(driver)
            .stream()
            .filter(webElement -> webElement.getAttribute("value").equals(dayOfWeek.name()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException(dayOfWeek + " not found."))
            .click();
    }

    public static void enableEditing(WebDriver driver) {
        DiaryPage.viewEventEditButton(driver)
            .click();
    }

    public static void editTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFillContentEditable(driver, DiaryPage.viewEventTitle(driver), title);
    }

    public static void saveModifications(WebDriver driver) {
        DiaryPage.viewEventSaveButton(driver)
            .click();
    }

    public static void editNote(WebDriver driver, String note) {
        clearAndFill(DiaryPage.viewEventNote(driver), note);
    }

    public static void editContent(WebDriver driver, String newContent) {
        clearAndFill(DiaryPage.viewEventContent(driver), newContent);
    }

    public static void fillRepetitionDays(WebDriver driver, int repetitionDays) {
        clearAndFill(DiaryPage.createEventRepetitionTypeDaysInput(driver), String.valueOf(repetitionDays));
    }
}
