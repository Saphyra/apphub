package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static com.github.saphyra.apphub.integration.frontend.framework.WebElementUtils.clearAndFill;
import static java.util.Objects.isNull;
import static org.assertj.core.api.Assertions.assertThat;

public class NotebookPageActions {
    public static void confirmDeletionDialog(WebDriver driver) {
        WebElement dialog = NotebookPage.deletionConfirmationDialog(driver);
        assertThat(dialog.isDisplayed()).isTrue();

        dialog.findElement(By.className("confirmation-dialog-confirm-button")).click();
    }

    public static void verifyEditListItemDialogOpened(WebDriver driver) {
        assertThat(NotebookPage.editListItemDialog(driver).isDisplayed()).isTrue();
    }

    public static void fillEditListItemDialog(WebDriver driver, String title, String value, String... parents) {
        verifyEditListItemDialogOpened(driver);

        clearAndFill(NotebookPage.editListItemTitleInput(driver), title);
        Optional.ofNullable(value)
            .ifPresent(s -> clearAndFill(NotebookPage.editListItemValueInput(driver), s));

        for (String parentTitle : parents) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.availableParentsForEditListItem(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }
    }

    public static void submitEditListItemDialog(WebDriver driver) {
        NotebookPage.saveEditedListItemButton(driver).click();
    }

    public static void verifyEditListItemDialogClosed(WebDriver driver) {
        assertThat(NotebookPage.editListItemDialog(driver).isDisplayed()).isFalse();
    }
}
