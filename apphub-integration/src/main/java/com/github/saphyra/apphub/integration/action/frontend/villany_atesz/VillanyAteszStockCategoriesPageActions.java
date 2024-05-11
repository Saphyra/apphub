package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockCategory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.List;
import java.util.stream.Collectors;

public class VillanyAteszStockCategoriesPageActions {
    public static void submitCreateForm(WebDriver driver) {
        driver.findElement(By.id("villany-atesz-stock-new-category-button"))
            .click();
    }

    public static void fillCreateForm(WebDriver driver, String name, String measurement) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-stock-new-category-name")), name);
        WebElementUtils.clearAndFill(driver.findElement(By.id("villany-atesz-stock-new-category-measurement")), measurement);
    }

    public static List<StockCategory> getStockCategories(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-category"))
            .stream()
            .map(StockCategory::new)
            .collect(Collectors.toList());
    }
}
