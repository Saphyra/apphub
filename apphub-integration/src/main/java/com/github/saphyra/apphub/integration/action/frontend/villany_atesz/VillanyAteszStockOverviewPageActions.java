package com.github.saphyra.apphub.integration.action.frontend.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.Cart;
import com.github.saphyra.apphub.integration.structure.view.villany_atesz.StockItemOverview;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class VillanyAteszStockOverviewPageActions {
    public static List<StockItemOverview> getItems(WebDriver driver) {
        return driver.findElements(By.className("villany-atesz-stock-overview-item"))
            .stream()
            .map(StockItemOverview::new)
            .collect(Collectors.toList());
    }

    public static String getActiveCartLabel(WebDriver driver) {
        return new Select(driver.findElement(By.id("villany-atesz-stock-overview-cart-selector")))
            .getFirstSelectedOption()
            .getText();
    }

    public static Optional<Cart> getCartDetails(WebDriver driver) {
        return WebElementUtils.getIfPresent(() -> driver.findElement(By.id("villany-atesz-stock-overview-cart-details")))
            .map(Cart::new);
    }
}
