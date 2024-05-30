package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemInventory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszStockInventoryPageActions {
    public static List<StockItemInventory> getItems(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-inventory-item"))
            .stream()
            .map(StockItemInventory::new)
            .collect(Collectors.toList());
    }

    public static void resetInventoried(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-inventory-reset-inventoried"))
            .click();

        driver.findElement(By.id("villany-atesz-stock-inventory-reset-inventoried-confirm-button"))
            .click();
    }
}
