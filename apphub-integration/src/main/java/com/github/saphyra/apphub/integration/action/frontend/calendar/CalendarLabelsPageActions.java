package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarLabel;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarLabelsPageActions {
    public static List<CalendarLabel> getLabels(WebDriver driver) {
        return driver.findElements(By.cssSelector(".calendar-labels-label.dynamic"))
            .stream()
            .map(CalendarLabel::new)
            .collect(Collectors.toList());
    }

    public static CalendarLabel getLabel(WebDriver driver, String label) {
        return getLabels(driver)
            .stream()
            .filter(calendarLabel -> calendarLabel.getLabel().equals(label))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Label not found: " + label));
    }

    public static List<WebElement> getEvents(WebDriver driver) {
        return driver.findElements(By.className("calendar-labels-event-title"));
    }

    public static WebElement getEvent(WebDriver driver, String defaultTitle) {
        return getEvents(driver)
            .stream()
            .filter(event -> event.getText().equals(defaultTitle))
            .findAny()
            .orElseThrow(() -> new IllegalStateException("Event not found: " + defaultTitle));
    }

    public static String getOpenedEventTitle(WebDriver driver) {
        return driver.findElement(By.id("calendar-labels-event-title"))
            .getText();
    }

    public static List<WebElement> getOpenedEventOccurrences(WebDriver driver) {
        return driver.findElements(By.className("calendar-labels-event-occurrence-date"));
    }

    public static LocalDate getOpenedOccurrenceDate(WebDriver driver) {
        return LocalDate.parse(driver.findElement(By.id("calendar-labels-opened-occurrence-title")).getText());
    }

    public static void selectNoLabelFilter(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-label-no-label"))
            .click();
    }
}
