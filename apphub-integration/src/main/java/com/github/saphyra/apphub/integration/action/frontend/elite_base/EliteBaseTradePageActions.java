package com.github.saphyra.apphub.integration.action.frontend.elite_base;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class EliteBaseTradePageActions {
    public static void setCommodity(WebDriver driver, String commodity) {
        By selector = By.id("elite-base-trade-commodity-selector");

        WebElementUtils.clearAndFill(driver.findElement(selector), commodity);

        AwaitilityWrapper.create(30, 1)
            .until(() -> WebElementUtils.getClasses(driver.findElement(selector)).contains("locked"))
            .assertTrue("Commodity not found by name: " + commodity);
    }

    public static void setReferenceSystem(WebDriver driver, String starName) {
        By selector = By.id("elite-base-trade-star-selector");

        WebElementUtils.clearAndFill(driver.findElement(selector), starName);

        AwaitilityWrapper.create(30, 1)
            .until(() -> WebElementUtils.getClasses(driver.findElement(selector)).contains("locked"))
            .assertTrue("Reference system not found by name: " + starName);
    }

    public static void search(WebDriver driver) {
        driver.findElement(By.id("elite-base-ct-search-button"))
            .click();
    }

    public static List<WebElement> getSearchResult(WebDriver driver) {
        return driver.findElements(By.cssSelector("#elite-base-ct-trade-commodities-search-result-table .elite-base-ct-search-result-item"));
    }
}
