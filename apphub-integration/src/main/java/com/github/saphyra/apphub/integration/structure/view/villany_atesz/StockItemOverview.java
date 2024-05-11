package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class StockItemOverview {
    private final WebElement webElement;

    public String getCategoryName() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-category"))
            .getText();
    }

    public String getName() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-name"))
            .getText();
    }

    public String getSerialNumber() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-serial-number"))
            .getText();
    }


    public String getInCar() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-in-car"))
            .getText();
    }

    public String getInStorage() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-in-storage"))
            .getText();
    }

    public String getPrice() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-price"))
            .getText();
    }

    public String getStockPrice() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-stock-value"))
            .getText();
    }

    public void toCar() {
        webElement.findElement(By.className("villany-atesz-stock-overview-item-move-to-car-button"))
            .click();
    }

    public void toStorage() {
        webElement.findElement(By.className("villany-atesz-stock-overview-item-move-to-storage-button"))
            .click();
    }

    public void setAmount(Integer amount) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-overview-item-add-to-cart-amount")), amount);
    }

    public void addToCart() {
        webElement.findElement(By.className("villany-atesz-stock-overview-item-add-to-cart-button"))
            .click();
    }

    public boolean isInCart() {
        return WebElementUtils.getClasses(webElement.findElement(By.className("villany-atesz-stock-overview-item-in-cart")))
            .contains("in-cart");
    }

    public String getInCart() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-item-in-cart"))
            .getText();
    }
}
