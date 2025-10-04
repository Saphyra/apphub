package com.github.saphyra.apphub.integration.structure.view.calendar;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class CalendarLabel {
    private final WebElement webElement;

    public String getLabel() {
        return webElement.findElement(By.className("calendar-labels-label-title"))
            .getText();
    }

    public CalendarLabel edit() {
        webElement.findElement(By.className("calendar-labels-label-edit"))
            .click();

        return this;
    }

    public CalendarLabel newLabel(WebDriver driver, String newLabel) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("calendar-labels-new-name")), newLabel);

        return this;
    }

    public CalendarLabel confirmNewLabel(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-edit-label-save"))
            .click();

        return this;
    }

    public CalendarLabel run(Runnable task) {
        task.run();

        return this;
    }

    public void cancelNewLabel(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-delete-label-cancel"))
            .click();
    }

    public CalendarLabel delete() {
        webElement.findElement(By.className("calendar-labels-label-delete"))
            .click();
        return this;
    }

    public void confirmDeletion(WebDriver driver) {
        driver.findElement(By.id("calendar-labels-delete-label-confirm"))
            .click();
    }

    public void open() {
        webElement.click();
    }
}
