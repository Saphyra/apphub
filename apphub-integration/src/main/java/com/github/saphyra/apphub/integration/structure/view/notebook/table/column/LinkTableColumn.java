package com.github.saphyra.apphub.integration.structure.view.notebook.table.column;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
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

    public void setLabel(String label) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("notebook-custom-table-column-link-label")), label);
    }

    public void setUrl(String url) {
        WebElementUtils.clearAndFill(webElement.findElement(By.className("notebook-custom-table-column-link-url")), url);
    }

    public String getLabel() {
        return webElement.findElement(By.className("table-column-wrapper"))
            .getText();
    }
}
