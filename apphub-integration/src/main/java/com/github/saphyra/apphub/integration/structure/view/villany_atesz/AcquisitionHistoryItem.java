package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class AcquisitionHistoryItem {
    private final WebElement webElement;

    public Integer getAmount() {
        return Integer.parseInt(webElement.findElement(By.className("villany-atesz-stock-acquisition-history-item-amount")).getText());
    }

    public String getItemName() {
        return webElement.findElement(By.className("villany-atesz-stock-acquisition-history-item-name"))
            .getText();
    }
}
