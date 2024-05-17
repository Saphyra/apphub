package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class VillanyAteszNavigation {
    public static void openContacts(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-contacts"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.VILLANY_ATESZ_CONTACTS_PAGE))
            .assertTrue("Contacts are not opened");
    }

    public static void openStockCategories(WebDriver driver) {
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-stock")))
            .ifPresent(WebElement::click);

        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("villany-atesz-stock-navigation-categories-button")), webElement -> true)
            .orElseThrow(() -> new RuntimeException("Stock Categories button not found."))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(() -> driver.findElement(By.id("villany-atesz-stock-new-category-name"))))
            .assertTrue("Stock Categories page is not loaded.");
    }

    public static void openStockNewItem(WebDriver driver) {
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-stock")))
            .ifPresent(WebElement::click);

        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("villany-atesz-stock-navigation-new-item-button")), webElement -> true)
            .orElseThrow(() -> new RuntimeException("New Item button not found."))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(() -> driver.findElement(By.id("villany-atesz-stock-new-item-create-button"))))
            .assertTrue("NewItem page is not loaded.");
    }

    public static void openStockOverview(WebDriver driver) {
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-stock")))
            .ifPresent(WebElement::click);

        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("villany-atesz-stock-navigation-overview-button")), webElement -> true)
            .orElseThrow(() -> new RuntimeException("New Item button not found."))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(() -> driver.findElement(By.id("villany-atesz-stock-overview-items-search"))))
            .assertTrue("Overview page is not loaded.");
    }

    public static void openAcquisition(WebDriver driver) {
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-stock")))
            .ifPresent(WebElement::click);

        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("villany-atesz-stock-navigation-acquisition-button")), webElement -> true)
            .orElseThrow(() -> new RuntimeException("New Item button not found."))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(() -> driver.findElement(By.id("villany-atesz-stock-acquisition-new-item-button"))))
            .assertTrue("Acquisition page is not loaded.");
    }

    public static void openStockInventory(WebDriver driver) {
        WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-stock")))
            .ifPresent(WebElement::click);

        AwaitilityWrapper.getWithWait(() -> driver.findElement(By.id("villany-atesz-stock-navigation-inventory-button")), webElement -> true)
            .orElseThrow(() -> new RuntimeException("New Item button not found."))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.isPresent(() -> driver.findElement(By.id("villany-atesz-stock-inventory-items-search"))))
            .assertTrue("Inventory page is not loaded.");
    }

    public static void openIndex(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-index"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-total-stock-value"))).isPresent())
            .assertTrue("Index page is not loaded.");
    }
}
