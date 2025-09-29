package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarDate;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOccurrence;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarIndexPageActions {
    public static void openCreateEventPage(WebDriver driver) {
        driver.findElement(By.id("calendar-selected-date-create-new"))
            .click();
    }

    public static void filterByLabel(WebDriver driver, String label) {
        driver.findElements(By.className("calendar-label"))
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
        return driver.findElements(By.className("calendar-content-day"))
            .stream()
            .map(webElement -> new CalendarDate(driver, webElement))
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
}
