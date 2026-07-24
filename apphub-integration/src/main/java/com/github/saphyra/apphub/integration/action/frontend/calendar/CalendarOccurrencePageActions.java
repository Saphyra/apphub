package com.github.saphyra.apphub.integration.action.frontend.calendar;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class CalendarOccurrencePageActions {
    public static void fill(WebDriver driver, OccurrenceParameters occurrence) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-edit-occurrence-date")), Optional.ofNullable(occurrence.getDate()).map(Object::toString).orElse(" "));
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-edit-occurrence-time")), occurrence.getTime());
        WebElementUtils.selectOptionByValue(driver.findElement(By.id("calendar-edit-occurrence-status")), occurrence.getStatus().name());
        setNote(driver, occurrence.getNote());
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-edit-occurrence-remind-me-before-days")), occurrence.getRemindMeBeforeDays());
        WebElementUtils.setCheckboxState(driver.findElement(By.id("calendar-edit-occurrence-reminded")), occurrence.isReminded());
    }

    public static void save(WebDriver driver) {
        driver.findElement(By.id("calendar-edit-occurrence-save-button"))
            .click();
    }

    public static boolean isReminded(WebDriver driver) {
        return driver.findElement(By.id("calendar-edit-occurrence-reminded"))
            .isSelected();
    }

    public static void setNote(WebDriver driver, String note) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-edit-occurrence-note")), note);
    }

    public static void share(WebDriver driver) {
        driver.findElement(By.id("calendar-edit-occurrence-share"))
            .click();
    }
}
