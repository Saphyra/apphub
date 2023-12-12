package com.github.saphyra.apphub.integration.structure.view.notebook.table.column;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class LinkTableColumn extends TableColumn{
    public LinkTableColumn(WebElement webElement) {
        super(webElement);
    }

    public void open() {
        webElement.findElement(By.cssSelector(".table-column-wrapper.button"))
            .click();
    }
}
