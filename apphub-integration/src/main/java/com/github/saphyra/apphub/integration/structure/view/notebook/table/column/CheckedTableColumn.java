package com.github.saphyra.apphub.integration.structure.view.notebook.table.column;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CheckedTableColumn extends TableColumn {
    public CheckedTableColumn(WebElement webElement) {
        super(webElement);
    }

    public void setChecked(boolean checked) {
        if (isChecked() != checked) {
            getCheckbox()
                .click();
        }
    }

    public boolean isChecked() {
        return getCheckbox()
            .isSelected();
    }

    private WebElement getCheckbox() {
        return webElement.findElement(By.cssSelector(".notebook-table-column-content input"));
    }
}
