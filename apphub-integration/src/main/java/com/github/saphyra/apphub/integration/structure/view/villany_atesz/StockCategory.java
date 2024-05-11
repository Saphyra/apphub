package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class StockCategory {
    private final WebElement webElement;

    public String getName() {
        if (editingEnabled()) {
            return webElement.findElement(By.className("villany-atesz-stock-category-name-input"))
                .getAttribute("value");
        } else {
            return webElement.findElement(By.className("villany-atesz-stock-category-name"))
                .getText();
        }
    }

    public String getMeasurement() {
        if (editingEnabled()) {
            return webElement.findElement(By.className("villany-atesz-stock-category-measurement-input"))
                .getAttribute("value");
        } else {
            return webElement.findElement(By.className("villany-atesz-stock-category-measurement"))
                .getText();
        }
    }

    public boolean editingEnabled() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("villany-atesz-stock-category-discard-button")));
    }

    public void edit() {
        webElement.findElement(By.className("villany-atesz-stock-category-edit-button"))
            .click();
    }

    public void setName(String name) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-category-name-input")), name);
    }

    public void save() {
        webElement.findElement(By.className("villany-atesz-stock-category-save-button"))
            .click();
    }

    public void setMeasurement(String measurement) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-category-measurement-input")), measurement);
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-stock-category-delete-button"))
            .click();

        driver.findElement(By.id("villany-atesz-stock-category-deletion-confirm-button"))
            .click();
    }
}