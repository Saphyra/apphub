package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.notebook.CategoryTreeElement;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

public class NotebookPageActions {
    public static void createCategory(WebDriver driver, String categoryTitle, String... parentTitles) {
        WebElement createCategoryWindow = NotebookPage.createCategoryWindow(driver);
        if (!createCategoryWindow.isDisplayed()) {
            openCreateCategoryWindow(driver);
        }

        NotebookPage.newCategoryTitleInput(driver).sendKeys(categoryTitle);

        for (String parentTitle : parentTitles) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.getAvailableParents(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }

        NotebookPage.saveNewCategoryButton(driver).click();

        AwaitilityWrapper.createDefault()
            .until(() -> !NotebookPage.createCategoryWindow(driver).isDisplayed());
    }

    private static void openCreateCategoryWindow(WebDriver driver) {
        new Actions(driver)
            .moveToElement(NotebookPage.newButton(driver))
            .perform();
        NotebookPage.openCreateCategoryWindowButton(driver).click();
        AwaitilityWrapper.createDefault()
            .until(() -> NotebookPage.createCategoryWindow(driver).isDisplayed());
    }

    public static CategoryTreeElement getCategoryTreeRoot(WebDriver driver) {
        return new CategoryTreeElement(NotebookPage.categoryTreeRoot(driver));
    }

    public static String getTitleOfOpenedCategory(WebDriver driver) {
        return NotebookPage.titleOfOpenedCategory(driver).getText();
    }

    public static List<ListItemDetailsItem> getDetailedListItems(WebDriver driver) {
        return NotebookPage.detailedListItems(driver)
            .stream()
            .map(ListItemDetailsItem::new)
            .collect(Collectors.toList());
    }
}
