package com.github.saphyra.apphub.integration.structure.notebook;

import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import static com.github.saphyra.apphub.integration.framework.WebElementUtils.clearAndFill;


@AllArgsConstructor
public class ViewChecklistItem {
    private static final By CONTENT_INPUT = By.cssSelector(":scope .view-checklist-item-content");
    private static final By CHECKBOX = By.cssSelector(":scope .view-checklist-item-checked-input");
    private static final By REMOVE_BUTTON = By.cssSelector(":scope .view-checklist-item-edit-button:last-child");

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

    public String getContent() {
        return webElement.findElement(CONTENT_INPUT).getText();
    }

    public boolean isChecked() {
        return getCheckbox().isSelected();
    }

    public void remove() {
        webElement.findElement(REMOVE_BUTTON).click();
    }
}
