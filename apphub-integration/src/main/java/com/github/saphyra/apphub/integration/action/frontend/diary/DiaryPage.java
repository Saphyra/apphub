package com.github.saphyra.apphub.integration.action.frontend.diary;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

class DiaryPage {
    public static WebElement dailyTasksCurrentDay(WebDriver driver) {
        return driver.findElement(By.id("daily-tasks-current-day"));
    }

    public static WebElement createTaskButton(WebDriver driver) {
        return driver.findElement(By.id("create-task-button"));
    }

    public static WebElement createEventPage(WebDriver driver) {
        return driver.findElement(By.id("create-event-page"));
    }

    public static WebElement createEventButton(WebDriver driver) {
        return driver.findElement(By.id("create-event-button"));
    }

    public static WebElement createEventTitleInput(WebDriver driver) {
        return driver.findElement(By.id("create-event-title-input"));
    }

    public static WebElement createEventContentInput(WebDriver driver) {
        return driver.findElement(By.id("create-event-content-input"));
    }

    public static List<WebElement> dailyTasks(WebDriver driver) {
        return driver.findElements(By.cssSelector("#daily-tasks .daily-task"));
    }

    public static WebElement viewEventPage(WebDriver driver) {
        return driver.findElement(By.id("view-event-page"));
    }

    public static WebElement viewEventTitle(WebDriver driver) {
        return driver.findElement(By.id("view-event-title"));
    }

    public static WebElement viewEventContent(WebDriver driver) {
        return driver.findElement(By.id("view-event-content"));
    }

    public static WebElement viewEventNote(WebDriver driver) {
        return driver.findElement(By.id("view-event-note"));
    }

    public static WebElement viewEventDoneButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-done-button"));
    }

    public static WebElement viewEventSnoozeButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-snooze-button"));
    }

    public static WebElement viewEventEditButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-edit-button"));
    }

    public static WebElement viewEventUnsnoozeButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-unsnooze-button"));
    }

    public static WebElement viewEventNotDoneButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-not-done-button"));
    }

    public static WebElement viewEventDeleteButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-delete-button"));
    }

    public static WebElement viewEventSaveButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-save-button"));
    }

    public static WebElement viewEventDiscardButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-discard-button"));
    }

    public static WebElement viewEventWindowCloseButton(WebDriver driver) {
        return driver.findElement(By.id("view-event-page-close-button"));
    }

    public static WebElement createEventRepetitionTypeSelect(WebDriver driver) {
        return driver.findElement(By.id("create-event-repetition-type-select"));
    }

    public static List<WebElement> createEventDaysOfWeekInputs(WebDriver driver) {
        return driver.findElements(By.className("create-event-days-of-week-input"));
    }

    public static WebElement createEventRepetitionTypeDaysInput(WebDriver driver) {
        return driver.findElement(By.id("create-event-repetition-type-days-input"));
    }

    public static WebElement previousMonthButton(WebDriver driver) {
        return driver.findElement(By.id("previous-month-button"));
    }

    public static WebElement nextMonthButton(WebDriver driver) {
        return driver.findElement(By.id("next-month-button"));
    }
}
