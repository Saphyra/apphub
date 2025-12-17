package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarDate;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarView;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarIndexPageActions {
    public static void openCreateEventPage(WebDriver driver) {
        driver.findElement(By.id("calendar-selected-date-create-new"))
            .click();

        WebElementUtils.waitForSpinnerToDisappear(driver);
    }

    public static void filterByLabel(WebDriver driver, String label) {
        getLabels(driver)
            .stream()
            .filter(webElement -> webElement.getText().equals(label))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Label not found: " + label))
            .click();
    }

    public static List<CalendarOccurrence> getSelectedDayOccurrences(WebDriver driver) {
        return driver.findElements(By.cssSelector("#calendar-selected-date-content .calendar-occurrence"))
            .stream()
            .map(CalendarOccurrence::new)
            .collect(Collectors.toList());
    }

    public static List<CalendarOccurrence> getOccurrencesOnDate(WebDriver driver, LocalDate date) {
        return getDays(driver)
            .stream()
            .filter(calendarDate -> !calendarDate.isFiller())
            .filter(calendarDate -> calendarDate.getDate().equals(date))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Date not found: " + date))
            .getOccurrences();
    }

    public static LocalDate getReferenceDate(WebDriver driver) {
        String value = driver.findElement(By.id("calendar-reference-date"))
            .getAttribute("value");
        return LocalDate.parse(value);
    }

    public static void clearLabelFilter(WebDriver driver) {
        driver.findElement(By.id("calendar-deselect-label-button"))
            .click();
    }

    public static CalendarOccurrence findOccurrenceByTitleOnDateValidated(WebDriver driver, LocalDate date, String title) {
        return getOccurrencesOnDate(driver, date).stream()
            .filter(occurrence -> occurrence.getTitle().equals(title))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Occurrence not found: " + title));
    }

    public static String getOpenedOccurrenceTitle(WebDriver driver) {
        return driver.findElement(By.id("calendar-selected-occurrence-title"))
            .getText();
    }

    public static void markOpenedOccurrenceDone(WebDriver driver) {
        driver.findElement(By.id("calendar-selected-occurrence-done-button"))
            .click();
    }

    public static CalendarOccurrence findOccurrenceByTitleOnSelectedDateValidated(WebDriver driver, String title) {
        return getSelectedDayOccurrences(driver)
            .stream()
            .filter(occurrence -> occurrence.getTitle().equals(title))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Occurrence not found: " + title));
    }

    public static void markOpenedOccurrenceSnoozed(WebDriver driver) {
        driver.findElement(By.id("calendar-selected-occurrence-snooze-button"))
            .click();
    }

    public static void resetOpenedOccurrenceStatus(WebDriver driver) {
        driver.findElement(By.id("calendar-selected-occurrence-reset-button"))
            .click();
    }

    public static void setReferenceDate(WebDriver driver, LocalDate referenceDate) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-reference-date")), referenceDate);
    }

    public static void deleteOccurrence(WebDriver driver) {
        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("calendar-selected-occurrence-delete-button")))
            .orElseThrow(() -> new IllegalStateException("No opened occurrence"))
            .click();

        driver.findElement(By.id("calendar-occurrence-delete-confirmation-button"))
            .click();
    }

    public static void deleteEvent(WebDriver driver) {
        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("calendar-selected-occurrence-delete-event-button")))
            .orElseThrow(() -> new IllegalStateException("No opened occurrence"))
            .click();

        driver.findElement(By.id("calendar-event-delete-confirmation-button"))
            .click();
    }

    public static void setView(WebDriver driver, CalendarView view) {
        WebElementUtils.waitForSpinnerToDisappear(driver);

        WebElementUtils.selectOptionByValue(driver.findElement(By.id("calendar-view-selector")), view.name());
    }

    public static List<CalendarDate> getDays(WebDriver driver) {
        return driver.findElements(By.className("calendar-content-day"))
            .stream()
            .map(webElement -> new CalendarDate(driver, webElement))
            .toList();
    }

    public static void toLabelsPage(WebDriver driver) {
        WebElementUtils.waitForSpinnerToDisappear(driver);

        driver.findElement(By.id("modify-labels-button"))
            .click();

        WebElementUtils.waitForSpinnerToDisappear(driver);
    }

    public static void editEvent(WebDriver driver) {
        AwaitilityWrapper.getOptionalWithWait(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-selected-occurrence-edit-event-button"))))
            .orElseThrow(() -> new IllegalStateException("No opened occurrence"))
            .click();
    }

    public static void editOccurrence(WebDriver driver) {
        AwaitilityWrapper.getOptionalWithWait(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-selected-occurrence-edit-button"))))
            .orElseThrow(() -> new IllegalStateException("No opened occurrence"))
            .click();
    }

    public static void confirmReminder(WebDriver driver) {
        AwaitilityWrapper.getOptionalWithWait(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("calendar-selected-occurrence-remind-button"))))
            .orElseThrow(() -> new IllegalStateException("No opened occurrence"))
            .click();
    }

    public static void closeOccurrence(WebDriver driver) {
        driver.findElement(By.id("calendar-selected-occurrence-cancel-button"))
            .click();
    }

    public static List<WebElement> getLabels(WebDriver driver) {
        return driver.findElements(By.className("calendar-label"));
    }
}
