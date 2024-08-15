package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.villany_atesz.ToolStatus;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class ToolInventoryItem {
    private final WebElement webElement;

    public void editToolType(String newToolType) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-tool-type")), newToolType);

        webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-tool-type-save-button"))
            .click();
    }

    public void editBrand(String newBrand) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-brand")), newBrand);
    }

    public void editName(String newName) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-name")), newName);
    }

    public void editStorageBox(String newStorageBox) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-storage-box")), newStorageBox);

        webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-storage-box-save-button"))
            .click();
    }

    public void editCost(Integer newCost) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-cost")), newCost);
    }

    public void setInventoried(boolean inventoried) {
        WebElementUtils.setCheckboxState(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-inventoried")), inventoried);
    }

    public void editStatus(ToolStatus toolStatus) {
        WebElementUtils.selectOptionByValue(webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-status")), toolStatus.name());
    }

    public boolean isInventoried() {
        return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-inventoried"))
            .isSelected();
    }

    public String getToolType() {
        if (isInventoried()) {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-tool-type"))
                .getText();
        } else {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-tool-type"))
                .getAttribute("value");
        }
    }

    public String getBrand() {
        if (isInventoried()) {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-brand"))
                .getText();
        } else {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-brand"))
                .getAttribute("value");
        }
    }

    public String getName() {
        if (isInventoried()) {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-name"))
                .getText();
        } else {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-name"))
                .getAttribute("value");
        }
    }

    public Integer getCost() {
        String value;
        if (isInventoried()) {
            value = webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-cost"))
                .getText();
        } else {
            value = webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-cost"))
                .getAttribute("value");
        }

        return Integer.parseInt(value);
    }

    public String getStorageBox() {
        if (isInventoried()) {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-storage-box"))
                .getText();
        } else {
            return webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-storage-box"))
                .getAttribute("value");
        }
    }

    public ToolStatus getToolStatus() {
        if (isInventoried()) {
            String value = webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-status"))
                .getText();

            return ToolStatus.fromLabel(value);
        } else {
            String value = webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-status"))
                .getAttribute("value");

            return ToolStatus.valueOf(value);
        }
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-toolbox-inventory-item-delete"))
            .click();

        driver.findElement(By.id("villany-atesz-toolbox-inventory-item-deletion-confirm-button"))
            .click();
    }
}
