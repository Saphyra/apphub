package com.github.saphyra.apphub.integration.structure.view.notebook.table.column;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

@RequiredArgsConstructor
public class TableColumn {
    @Getter
    protected final WebElement webElement;

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

    public void openColumnTypeSelector() {
        webElement.findElement(By.className("notebook-table-change-column-type-button"))
            .click();
    }

    public boolean isColumnTypeSelectorOpened() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.className("notebook-table-column-type-selector")))
            .isPresent();
    }

    public void setColumnType(ColumnType checkbox) {
        webElement.findElement(By.className(checkbox.getColumnTypeSelectorClass()))
            .click();
    }

    public <T extends TableColumn> T as(ColumnType columnType){
        return (T) columnType.getConverter()
            .apply(this);
    }
}
