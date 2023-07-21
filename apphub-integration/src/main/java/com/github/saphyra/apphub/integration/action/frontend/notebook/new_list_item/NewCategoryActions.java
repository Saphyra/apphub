package com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item;

import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NewCategoryActions {
    public static void create(WebDriver driver, String title) {
        fillTitle(driver, title);
        submitForm(driver);

        NotebookActions.waitForPageOpened(driver);
    }

    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-category-title")), title);
    }

    public static void submitForm(WebDriver driver) {
        driver.findElement(By.id("notebook-new-category-create-button"))
            .click();
    }
}
