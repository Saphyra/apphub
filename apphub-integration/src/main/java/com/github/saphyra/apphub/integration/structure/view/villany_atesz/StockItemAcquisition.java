package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class StockItemAcquisition {
    private final WebElement webElement;

    public void remove() {
        webElement.findElement(By.className("villany-atesz-stock-acquisition-item-remove-button"))
            .click();
    }

    public void chooseCategory(String categoryName) {
        WebElementUtils.selectOptionByLabel(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-category")), categoryName);
    }

    public void chooseItem(String stockItemName) {
        WebElementUtils.selectOptionByLabel(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-item")), stockItemName);
    }

    public void setToCar(Integer toCar) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-to-car")), toCar);
    }

    public void setToStorage(Integer toStorage) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-to-storage")), toStorage);
    }

    public void setPrice(Integer price) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-price")), price);
    }

    public void setBarCodeSearch(String barCode) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-bar-code-search")), barCode);
    }

    public String getCategory() {
        return WebElementUtils.getSelectedOptionLabel(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-category")));
    }

    public String getItemName() {
        return WebElementUtils.getSelectedOptionLabel(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-item")));
    }

    public String getBarCode() {
        return webElement.findElement(By.className("villany-atesz-stock-acquisition-item-bar-code"))
            .getAttribute("value");
    }

    public void setBarCode(String newBarCode) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-stock-acquisition-item-bar-code")), newBarCode);
    }
}
