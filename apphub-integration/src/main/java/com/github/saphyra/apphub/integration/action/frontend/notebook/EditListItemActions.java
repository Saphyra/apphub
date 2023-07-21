package com.github.saphyra.apphub.integration.action.frontend.notebook;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class EditListItemActions {
    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-edit-list-item-title")), title);
    }

    public static void submitForm(WebDriver driver) {
        driver.findElement(By.id("notebook-edit-save-button"))
            .click();
    }
}
