package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class Cart {
    private final WebElement webElement;

    public Integer getTotalValue() {
        return Integer.parseInt(webElement.findElement(By.id("villany-atesz-stock-overview-cart-details-total-price")).getText());
    }

    public List<CartItem> getItems() {
        return webElement.findElements(By.className("villany-atesz-stock-overview-cart-item"))
            .stream()
            .map(CartItem::new)
            .collect(Collectors.toList());
    }

    public void delete(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-overview-delete-cart"))
            .click();

        driver.findElement(By.id("villany-atesz-stock-delete-cart-confirm-button"))
            .click();
    }

    public void finalize(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-overview-finalize-cart"))
            .click();

        driver.findElement(By.id("villany-atesz-stock-finalize-cart-confirm-button"))
            .click();
    }

    public void setMargin(int margin) {
        WebElementUtils.clearAndFill(webElement.findElement(By.id("villany-atesz-stock-overview-cart-details-margin")), margin);
    }
}