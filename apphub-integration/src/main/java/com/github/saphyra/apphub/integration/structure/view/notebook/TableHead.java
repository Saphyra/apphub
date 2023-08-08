package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class TableHead {
    private final WebElement webElement;

    public void remove() {
        webElement.findElement(By.cssSelector(":scope .notebook-table-head-remove-button"))
            .click();
    }

    public void setValue(String value) {
        WebElementUtils.clearAndFill(getInputField(), value);
    }

    private WebElement getInputField() {
        return webElement.findElement(By.cssSelector(":scope .notebook-table-head-content"));
    }

    public void moveRight() {
        webElement.findElement(By.cssSelector(":scope .notebook-table-head-move-right-button"))
            .click();
    }

    public String getValue() {
        if (isEditingEnabled()) {
            return getInputField()
                .getAttribute("value");
        } else {
            return webElement.findElement(By.cssSelector(":scope .notebook-table-head-content"))
                .getText();
        }
    }

    public void moveLeft() {
        webElement.findElement(By.cssSelector(":scope .notebook-table-head-move-left-button"))
            .click();
    }

    public boolean isEditingEnabled() {
        return WebElementUtils.getClasses(webElement)
            .contains("editable");
    }
}
