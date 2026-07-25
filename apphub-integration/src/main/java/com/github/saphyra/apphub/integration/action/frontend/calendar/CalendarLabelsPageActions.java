package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarLabel;
import com.github.saphyra.apphub.integration.structure.view.calendar.CalendarOpenedEventOccurrence;
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
            .orElseThrow(() -> new IllegalStateException("Label not found: " + label + ". Labels: " + getLabels(driver).stream().map(CalendarLabel::getLabel).collect(Collectors.joining(", "))));
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
        return driver.findElement(By.id("calendar-opened-event-title"))
            .getText();
    }

    public static List<CalendarOpenedEventOccurrence> getOpenedEventOccurrences(WebDriver driver) {
        return driver.findElements(By.className("calendar-opened-event-occurrence"))
            .stream()
            .map(CalendarOpenedEventOccurrence::new)
            .collect(Collectors.toList());
    }

    public static LocalDate getOpenedOccurrenceDate(WebDriver driver) {
        return LocalDate.parse(driver.findElement(By.id("calendar-opened-occurrence-title")).getText().replace(Constants.SHARED_SUFFIX, ""));
    }

    public static void selectNoLabelFilter(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-label-no-label"))
            .click();
    }

    public static void mergeEvents(WebDriver driver) {
        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("calendar-opened-event-merge-button")))
            .orElseThrow(() -> new IllegalStateException("Event not found"))
            .click();

        driver.findElement(By.id("calendar-opened-event-merge-confirmation-dialog-confirm"))
            .click();
    }

    public static void toggleArchiveOpenedEvent(WebDriver driver) {
        driver.findElement(By.id("calendar-opened-event-archive-button"))
            .click();
    }

    public static boolean isOpenedOccurrenceShared(WebDriver driver) {
        return driver.findElement(By.id("calendar-opened-occurrence-title"))
            .getText()
            .endsWith(Constants.SHARED_SUFFIX);
    }

    public static void editOpenedEvent(WebDriver driver) {
        driver.findElement(By.id("calendar-opened-event-edit"))
            .click();
    }

    public static void back(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-back-button"))
            .click();
    }
}
