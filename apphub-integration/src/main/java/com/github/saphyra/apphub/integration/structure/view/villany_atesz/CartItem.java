package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@AllArgsConstructor
public class CartItem {
    private final WebElement webElement;

    public Integer getAmount() {
        return  Integer.parseInt(webElement.findElement(By.className("villany-atesz-stock-overview-cart-item-amount")).getText());
    }

    public String getName() {
        return webElement.findElement(By.className("villany-atesz-stock-overview-cart-item-name")).getText();
    }

    public void remove(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-stock-overview-cart-item-remove-button"))
            .click();

        driver.findElement(By.id("villany-atesz-stock-overview-cart-item-removal-confirm-button"))
            .click();
    }
}
