package com.github.saphyra.apphub.integration.structure.api.notebook;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;

@AllArgsConstructor
public class NewChecklistItem {
    private static final By CONTENT_INPUT = By.cssSelector(":scope .checklist-item-content");
    private static final By CHECKBOX = By.cssSelector(":scope .checklist-item-options-checked");

    private final WebElement webElement;

    public void setContent(String content) {
        clearAndFill(webElement.findElement(CONTENT_INPUT), content);
    }

    public void setStatus(boolean checked) {
        WebElement checkbox = getCheckbox();

        if (isChecked() != checked) {
            checkbox.click();
        }
    }

    private WebElement getCheckbox() {
        return webElement.findElement(CHECKBOX);
    }

    public boolean isChecked() {
        return getCheckbox().isSelected();
    }
}
