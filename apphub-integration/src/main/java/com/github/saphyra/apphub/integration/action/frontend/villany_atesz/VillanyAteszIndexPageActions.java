package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszIndexPageActions {
    public static Integer getTotalValue(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-total-stock-value-value")).getText());
    }

    public static List<String> getItemsMarkedForAcquisition(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-item-marked-for-acquisition"))
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }
}
