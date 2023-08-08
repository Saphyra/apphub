package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class TableColumn {
    private final WebElement webElement;

    public void setValue(String value) {
        WebElementUtils.clearAndFill(getInput(), value);
    }

    private WebElement getInput() {
        return webElement.findElement(By.cssSelector(":scope .notebook-table-column-input"));
    }

    public String getValue() {
        if (isEditingEnabled()) {
            return getInput()
                .getAttribute("value");
        } else {
            return webElement.getText();
        }
    }

    public boolean isEditingEnabled() {
        return WebElementUtils.getClasses(webElement)
            .contains("editable");
    }
}
