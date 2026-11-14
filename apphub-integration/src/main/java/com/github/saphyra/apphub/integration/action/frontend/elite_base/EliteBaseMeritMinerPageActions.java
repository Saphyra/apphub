package com.github.saphyra.apphub.integration.action.frontend.elite_base;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EliteBaseMeritMinerPageActions {
    public static void load(WebDriver driver) {
        driver.findElement(By.id("elite-base-merit-miner-display-more-button"))
            .click();
    }

    public static List<WebElement> getOffers(WebDriver driver) {
        return driver.findElements(By.cssSelector("#elite-base-merit-miner-search-result .elite-base-merit-miner-search-result-item"));
    }
}
