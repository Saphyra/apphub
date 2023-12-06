package com.github.saphyra.apphub.integration.structure.view.notebook.table.column;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class NumberTableColumn extends TableColumn {
    public NumberTableColumn(WebElement webElement) {
        super(webElement);
    }

    public void setValue(int value) {
        WebElementUtils.clearAndFill(valueInput(), value);
    }

    private WebElement valueInput() {
        return webElement.findElement(By.className("notebook-table-column-data-number-value"));
    }

    public void setStep(int step) {
        WebElementUtils.clearAndFill(getStepInput(), step);
    }

    private WebElement getStepInput() {
        return webElement.findElement(By.className("notebook-table-column-data-number-step"));
    }

    public String getValue() {
        if (isEditingEnabled()) {
            return valueInput()
                .getAttribute("value");
        }

        return super.getValue();
    }

    public String getStep() {
        if (isEditingEnabled()) {
            return getStepInput()
                .getAttribute("value");
        }

        throw new UnsupportedOperationException("Step is not displayed if editing is not enabled");
    }
}
