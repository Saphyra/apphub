package com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class NewTableActions {
    public static void fillTitle(WebDriver driver, String title) {
        WebElementUtils.clearAndFill(driver.findElement(By.id("notebook-new-table-title")), title);
    }

    public static void submit(WebDriver driver) {
        driver.findElement(By.id("notebook-new-table-create-button"))
            .click();
    }
}
