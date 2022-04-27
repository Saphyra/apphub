package com.github.saphyra.apphub.integration.action.frontend.common;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonPageActions {
    public static void confirmConfirmationDialog(WebDriver driver, String id) {
        AwaitilityWrapper.createDefault()
            .until(() -> {
                Optional<WebElement> maybeConfirmationDialog = WebElementUtils.getIfPresent(() -> driver.findElement(By.id(id)));
                return maybeConfirmationDialog.isPresent() && maybeConfirmationDialog.get().isDisplayed();
            })
            .assertTrue("Confirmation dialog not displayed with id " + id);

        WebElement dialog = CommonPage.confirmationDialog(driver, id);
        assertThat(dialog.isDisplayed()).isTrue();

        dialog.findElement(By.cssSelector(String.format("#%s .confirmation-dialog-confirm-button", id))).click();
    }
}
