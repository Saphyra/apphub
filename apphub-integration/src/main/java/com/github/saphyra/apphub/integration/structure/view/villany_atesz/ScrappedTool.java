package com.github.saphyra.apphub.integration.structure.view.villany_atesz;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.LocalDate;

@RequiredArgsConstructor
public class ScrappedTool {
    private final WebElement webElement;

    public LocalDate getScrappedAt() {
        return LocalDate.parse(webElement.findElement(By.className("villany-atesz-toolbox-scrapped-item-scrapped-at")).getText());
    }

    public void descrap() {
        webElement.findElement(By.className("villany-atesz-toolbox-scrapped-item-set-to-default-button"))
            .click();
    }

    public void delete(WebDriver driver) {
        webElement.findElement(By.className("villany-atesz-toolbox-scrapped-item-delete-button"))
            .click();

        driver.findElement(By.id("villany-atesz-toolboc-scrapped-deletion-confirm-button"))
            .click();
    }
}
