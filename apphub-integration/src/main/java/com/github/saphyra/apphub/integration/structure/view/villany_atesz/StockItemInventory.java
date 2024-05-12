package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class StockItemInventory {
    private final WebElement webElement;

    public void setInventoried(boolean inventoried) {
        WebElementUtils.setCheckboxState(webElement.findElement(By.className("villany-atesz-stock-inventory-item-inventoried")), inventoried);
    }

    public void setCategory(String categoryName) {
        WebElementUtils.selectOptionByLabel(webElement.findElement(By.className("villany-atesz-stock-inventory-item-category")), categoryName);
    }

    public void setName(String name) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-inventory-item-name")), name);
    }

    public void setSerialNumber(String serialNumber) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-inventory-item-serial-number")), serialNumber);
    }

    public void setInCar(Integer inCar) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-inventory-item-in-car")), inCar);
    }

    public void setInStorage(Integer inStorage) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-inventory-item-in-storage")), inStorage);
    }

    public boolean isNamePending() {
        return WebElementUtils.getClasses(webElement.findElement(By.className("villany-atesz-stock-inventory-item-name")))
            .contains("scheduled");
    }

    public boolean isInventoried() {
        return webElement.findElement(By.className("villany-atesz-stock-inventory-item-inventoried"))
            .isSelected();
    }

    public boolean isInCart() {
        return !webElement.findElement(By.className("villany-atesz-stock-inventory-item-in-car"))
            .isEnabled();
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-stock-inventory-item-delete-button"))
            .click();

        driver.findElement(By.id("villany-atesz-stock-inventory-item-confirm-delete-button"))
            .click();
    }

    public void setAmount(Integer amount) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-inventory-item-amount")), amount);
    }

    public void moveToCar() {
        webElement.findElement(By.className("villany-atesz-stock-inventory-item-move-to-car-button"))
            .click();
    }

    public void moveToStorage() {
        webElement.findElement(By.className("villany-atesz-stock-inventory-item-move-to-storage-button"))
            .click();
    }

    public Integer getInCar() {
        return Integer.parseInt(webElement.findElement(By.className("villany-atesz-stock-inventory-item-in-car")).getAttribute("value"));
    }

    public Integer getInStorage() {
        return Integer.parseInt(webElement.findElement(By.className("villany-atesz-stock-inventory-item-in-storage")).getAttribute("value"));
    }
}
