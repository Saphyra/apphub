package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class VillanyAteszStockNewItemPageActions {
    public static void fillForm(WebDriver driver, String stockCategoryName, String stockItemName, String serialNumber, String barCode, Integer inCar, Integer inStorage, Integer price) {
        WebElementUtils.selectOptionByLabel(driver.findElement(By.id("villany-atesz-new-item-category")), stockCategoryName);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-name")), stockItemName);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-serial-number")), serialNumber);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-bar-code")), barCode);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-in-car")), inCar);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-in-car")), inCar);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-in-storage")), inStorage);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-new-item-price")), price);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-new-item-create-button"))
            .click();
    }
}
