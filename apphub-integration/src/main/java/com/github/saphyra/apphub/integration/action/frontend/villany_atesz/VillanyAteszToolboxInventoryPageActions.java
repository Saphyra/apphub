package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolInventoryItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszToolboxInventoryPageActions {
    public static List<ToolInventoryItem> getItems(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-toolbox-inventory-item"))
            .stream()
            .map(ToolInventoryItem::new)
            .collect(Collectors.toList());
    }

    public static void resetInventoried(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-toolbox-reset-inventoried-button"))
            .click();

        driver.findElement(By.id("villany-atesz-toolbox-inventory-reset-inventoried-confirm-button"))
            .click();
    }
}
