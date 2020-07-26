package com.github.saphyra.apphub.integration.frontend.service.notebook;

import com.github.saphyra.apphub.integration.frontend.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.model.notebook.CategoryTreeElement;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import static java.util.Objects.isNull;

public class CategoryActions {
    public static void createCategory(WebDriver driver, String categoryTitle, String... parentTitles) {
        if (!isCreateCategoryWindowDisplayed(driver)) {
            openCreateCategoryWindow(driver);
        }

        fillNewCategoryTitle(driver, categoryTitle);

        for (String parentTitle : parentTitles) {
            AwaitilityWrapper.getWithWait(
                () -> NotebookPage.availableParentsForNewCategory(driver).stream()
                    .filter(webElement -> webElement.getText().equals(parentTitle))
                    .findFirst()
                    .orElse(null),
                webElement -> !isNull(webElement)
            ).orElseThrow(() -> new RuntimeException("No available parent found with name " + parentTitle))
                .click();
        }

        submitCreateCategoryForm(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !NotebookPage.createCategoryWindow(driver).isDisplayed());
    }

    public static void fillNewCategoryTitle(WebDriver driver, String categoryTitle) {
        NotebookPage.newCategoryTitleInput(driver).sendKeys(categoryTitle);
    }

    public static boolean isCreateCategoryWindowDisplayed(WebDriver driver) {
        WebElement createCategoryWindow = NotebookPage.createCategoryWindow(driver);
        return createCategoryWindow.isDisplayed();
    }

    public static void submitCreateCategoryForm(WebDriver driver) {
        NotebookPage.saveNewCategoryButton(driver).click();
    }

    public static void openCreateCategoryWindow(WebDriver driver) {
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

    public static void deleteCategory(WebDriver driver, String title) {
        ListItemDetailsItem categoryToDelete = DetailedListActions.findDetailedItem(driver, title);

        categoryToDelete.delete(driver);
    }

    public static void openCategory(WebDriver driver, String title){
        DetailedListActions.findDetailedItem(driver, title).open();

        AwaitilityWrapper.createDefault()
            .until(() -> DetailedListActions.getTitleOfOpenedCategory(driver).equals(title))
            .assertTrue();
    }
}
