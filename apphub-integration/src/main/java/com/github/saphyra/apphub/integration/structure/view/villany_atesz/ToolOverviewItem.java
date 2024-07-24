package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;
import java.util.List;

@RequiredArgsConstructor
public class ToolOverviewItem {
    private final WebElement webElement;

    public String getBrand() {
        return webElement.findElement(By.className("villany-atesz-toolbox-overview-item-brand"))
            .getText();
    }

    public String getName() {
        return webElement.findElement(By.className("villany-atesz-toolbox-overview-item-name"))
            .getText();
    }

    public Integer getCost() {
        return Integer.parseInt(webElement.findElement(By.className("villany-atesz-toolbox-overview-item-cost")).getText());
    }

    public LocalDate getAcquiredAt() {
        return LocalDate.parse(webElement.findElement(By.className("villany-atesz-toolbox-overview-item-acquired-at")).getText());
    }

    public ToolStatus getStatus() {
        List<String> classes = WebElementUtils.getClasses(webElement);

        if (classes.contains("background-orange")) {
            return ToolStatus.DAMAGED;
        }

        if (classes.contains("background-red")) {
            return ToolStatus.LOST;
        }

        return ToolStatus.DEFAULT;
    }

    public void markAsLost() {
        webElement.findElement(By.className("villany-atesz-toolbox-overview-item-set-to-lost-button"))
            .click();
    }

    public void markAsDamaged() {
        webElement.findElement(By.className("villany-atesz-toolbox-overview-item-set-to-damaged-button"))
            .click();
    }

    public void scrap() {
        webElement.findElement(By.className("villany-atesz-toolbox-overview-item-set-to-scrapped-button"))
            .click();
    }
}
