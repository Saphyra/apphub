package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class ToolboxManagedStorageBox {
    private final WebElement webElement;

    public void edit(String newName) {
        if (!editingEnabled()) {
            enableEditing();
        }

        WebElementUtils.clearAndFill(webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-name")), newName);

        webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-save-button"))
            .click();
    }

    private void enableEditing() {
        webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-enable-editing-button"))
            .click();
    }

    public boolean editingEnabled() {
        return WebElementUtils.isPresent(() -> webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-save-button")));
    }

    public String getName() {
        if (editingEnabled()) {
            return webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-name"))
                .getAttribute("value");
        } else {
            return webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-name"))
                .getText();
        }
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-toolbox-manage-storage-box-delete-button"))
            .click();

        driver.findElement(By.id("villany-atesz-toolbox-manage-storage-box-deletion-confirm-button"))
            .click();
    }
}
