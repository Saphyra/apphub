package com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NewTextActions {
    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-text-title")), title);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("notebook-new-text-create-button"))
            .click();
    }

    public static void fillContent(WebDriver driver, String content) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-text-content")), content);
    }
}
