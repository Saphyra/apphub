package com.github.saphyra.apphub.integration.frontend.service.common;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonPageActions {
    public static void confirmDeletionDialog(WebDriver driver, String id) {
        WebElement dialog = CommonPage.deletionConfirmationDialog(driver, id);
        assertThat(dialog.isDisplayed()).isTrue();

        dialog.findElement(By.cssSelector(String.format("#%s .confirmation-dialog-confirm-button", id))).click();
    }
}
