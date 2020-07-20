package com.github.saphyra.apphub.integration.frontend.service.notebook;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import static org.assertj.core.api.Assertions.assertThat;

public class NotebookPageActions {
    public static void confirmDeletionDialog(WebDriver driver) {
        WebElement dialog = NotebookPage.deletionConfirmationDialog(driver);
        assertThat(dialog.isDisplayed()).isTrue();

        dialog.findElement(By.className("confirmation-dialog-confirm-button")).click();
    }
}
