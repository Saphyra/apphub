package com.github.saphyra.apphub.integration.structure.view.notebook;

import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

@RequiredArgsConstructor
public class CategoryTreeLeaf {
    private final WebElement webElement;

    public List<CategoryTreeLeaf> getChildren() {
        return webElement.findElements(By.cssSelector(":scope .notebook-tree-leaf-children > .notebook-tree-leaf-wrapper"))
            .stream()
            .map(CategoryTreeLeaf::new)
            .toList();
    }

    public boolean isArchived() {
        return WebElementUtils.getClasses(webElement.findElement(By.cssSelector(":scope .notebook-tree-leaf")))
            .contains("archived");
    }
}
