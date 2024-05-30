package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class VillanyAteszIndexPageActions {
    public static Integer getTotalValue(WebDriver driver) {
        return Integer.parseInt(driver.findElement(By.id("villany-atesz-total-stock-value-value")).getText());
    }
}
