package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class TableRow {
    private final WebElement webElement;

    public void remove() {
        webElement.findElement(By.cssSelector(":scope .notebook-table-row-remove-button"))
            .click();
    }

    public List<TableColumn> getColumns() {
        return webElement.findElements(By.cssSelector(":scope .table-column"))
            .stream()
            .map(TableColumn::new)
            .collect(Collectors.toList());
    }

    public void check() {
        if (!isChecklistRow()) {
            throw new IllegalStateException("Row is not a checklist row.");
        }

        WebElement checkbox = getCheckbox();

        if (checkbox.isSelected()) {
            throw new IllegalStateException("Row is already checked.");
        }

        checkbox.click();
    }

    private WebElement getCheckbox() {
        return webElement.findElement(By.cssSelector(":scope .notebook-table-row-checked"));
    }

    public boolean isChecklistRow() {
        return WebElementUtils.getIfPresent(() -> webElement.findElement(By.cssSelector(":scope .notebook-table-row-checked-cell")))
            .isPresent();
    }

    public boolean isChecked() {
        return getCheckbox()
            .isSelected();
    }

    public void moveDown() {
        webElement.findElement(By.cssSelector(":scope .notebook-table-row-move-down-button"))
            .click();
    }

    public void moveUp() {
        webElement.findElement(By.cssSelector(":scope .notebook-table-row-move-up-button"))
            .click();
    }

    public void uncheck() {
        if (!isChecklistRow()) {
            throw new IllegalStateException("Row is not a checklist row.");
        }

        WebElement checkbox = getCheckbox();

        if (!checkbox.isSelected()) {
            throw new IllegalStateException("Row is already unchecked.");
        }

        checkbox.click();
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
}
