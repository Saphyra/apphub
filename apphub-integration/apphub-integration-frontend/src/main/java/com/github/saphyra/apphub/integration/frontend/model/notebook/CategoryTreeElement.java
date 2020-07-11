package com.github.saphyra.apphub.integration.frontend.model.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import lombok.AllArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
public class CategoryTreeElement {
    private static final By CHILDREN = By.cssSelector(":scope > .category-children-container > .category-wrapper");
    private static final By CATEGORY_BUTTON = By.cssSelector(":scope > .category-view-button");
    private static final By CHILDREN_CONTAINER = By.cssSelector(":scope > .category-children-container");
    private static final By OPEN_CHILDREN_LIST_BUTTON = By.cssSelector(":scope > .category-view-button .category-view-toggle-button");

    private final WebElement webElement;

    public List<CategoryTreeElement> getChildren() {
        return webElement.findElements(CHILDREN)
            .stream()
            .map(CategoryTreeElement::new)
            .collect(Collectors.toList());
    }

    public void openInMainWindow() {
        webElement.findElement(CATEGORY_BUTTON).click();
    }

    public void openChildrenList() {
        WebElement childrenContainer = webElement.findElement(CHILDREN_CONTAINER);
        if(childrenContainer.isDisplayed()){
            return;
        }
        WebElement childListButton = webElement.findElement(OPEN_CHILDREN_LIST_BUTTON);
        childListButton.click();

        AwaitilityWrapper.createDefault()
            .until(childrenContainer::isDisplayed)
            .assertTrue();
    }
}
