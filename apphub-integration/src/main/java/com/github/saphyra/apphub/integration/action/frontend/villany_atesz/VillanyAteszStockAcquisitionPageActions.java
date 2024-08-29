package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.structure.view.villany_atesz.AcquisitionHistoryItem;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemAcquisition;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszStockAcquisitionPageActions {
    public static List<StockItemAcquisition> getItems(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-acquisition-item"))
            .stream()
            .map(StockItemAcquisition::new)
            .collect(Collectors.toList());
    }

    public static void addToStock(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-acquisition-add-to-stock-button"))
            .click();
    }

    public static void openHistory(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-acquisition-show-history-button"))
            .click();
    }

    public static List<AcquisitionHistoryItem> getHistoryItems(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-acquisition-history-item"))
            .stream()
            .map(AcquisitionHistoryItem::new)
            .collect(Collectors.toList());
    }
}
