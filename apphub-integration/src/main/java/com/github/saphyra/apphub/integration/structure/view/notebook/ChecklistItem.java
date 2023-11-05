package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class ChecklistItem {
    private final WebElement webElement;

    public void setValue(String value) {
        WebElementUtils.clearAndFill(getInputField(), value);
    }

    private WebElement getInputField() {
        return webElement.findElement(By.cssSelector(":scope .notebook-checklist-item-content"));
    }

    public void remove() {
        webElement.findElement(By.cssSelector(":scope .notebook-checklist-item-remove-button"))
            .click();
    }

    public String getValue() {
        if (isEditingEnabled()) {
            return getInputField()
                .getAttribute("value");
        } else {
            return webElement.findElement(By.cssSelector(":scope .notebook-checklist-item-content-not-editable"))
                .getText();
        }

    }

    private boolean isEditingEnabled() {
        return WebElementUtils.getClasses(webElement)
            .contains("editable");
    }

    public void moveDown() {
        webElement.findElement(By.cssSelector(":scope .notebook-checklist-item-move-down-button"))
            .click();
    }

    public void moveUp() {
        webElement.findElement(By.cssSelector(":scope .notebook-checklist-item-move-up-button"))
            .click();
    }

    public void check() {
        if (isChecked()) {
            throw new IllegalStateException("ChecklistItem is already checked.");
        }

        getCheckbox()
            .click();
    }

    public boolean isChecked() {
        return getCheckbox()
            .isSelected();
    }

    private WebElement getCheckbox() {
        return webElement.findElement(By.cssSelector(":scope .notebook-checklist-item-checked"));
    }

    public void uncheck() {
        if (!isChecked()) {
            throw new IllegalStateException("ChecklistItem is not checked.");
        }

        getCheckbox()
            .click();
    }

    public void setChecked(boolean checked) {
        if (checked) {
            if (!isChecked()) {
                check();
            }
        } else {
            if (isChecked()) {
                uncheck();
            }
        }
    }

    public void edit(WebDriver driver, String newContent) {
        webElement.findElement(By.className("notebook-checklist-item-edit-button"))
            .click();

        WebElementUtils.clearAndFill(getEditContentInput(driver), newContent);

        driver.findElement(By.id("notebook-checklist-item-content-edit-save-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> WebElementUtils.getIfPresent(() -> getEditContentInput(driver)).isEmpty())
            .assertTrue("Edit checklist item content dialog did not disappear");
    }

    private static WebElement getEditContentInput(WebDriver driver) {
        return driver.findElement(By.id("notebook-checklist-item-edit-input"));
    }
}
