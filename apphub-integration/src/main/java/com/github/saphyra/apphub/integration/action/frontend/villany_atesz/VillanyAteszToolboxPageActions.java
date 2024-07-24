package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ScrappedTool;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.ToolOverviewItem;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszToolboxPageActions {
    public static void fillForm(WebDriver driver, String brand, String name, Integer cost) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-toolbox-new-brand")), brand);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-toolbox-new-name")), name);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-toolbox-new-cost")), cost);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-toolbox-new-create"))
            .click();
    }

    public static List<ToolOverviewItem> getOverviewTools(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-toolbox-overview-item"))
            .stream()
            .map(ToolOverviewItem::new)
            .collect(Collectors.toList());
    }

    public static List<ScrappedTool> getScrappedTools(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-toolbox-scrapped-item"))
            .stream()
            .map(ScrappedTool::new)
            .collect(Collectors.toList());
    }
}
