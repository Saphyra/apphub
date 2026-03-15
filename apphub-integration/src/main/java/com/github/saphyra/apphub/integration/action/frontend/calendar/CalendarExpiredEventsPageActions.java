package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;

public class CalendarExpiredEventsPageActions {
    public static List<WebElement> getEvents(WebDriver driver) {
        return driver.findElements(By.cssSelector("#calendar-expired-event-list .calendar-expired-event"));
    }

    public static void hideExpiredEvent(WebDriver driver) {
        driver.findElement(By.id("calendar-expired-event-hide"))
            .click();

        driver.findElement(By.id("calendar-expired-event-hide-button"))
            .click();
    }

    public static void extendExpiredEvent(WebDriver driver) {
        driver.findElement(By.id("calendar-expired-event-extend-end-date-button"))
            .click();
    }

    public static void setExtendUntil(WebDriver driver, LocalDate extendUntil) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-expired-event-extend-end-date-input")), extendUntil);
    }
}
