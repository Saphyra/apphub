package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.EditListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookUtils;
import com.github.saphyra.apphub.integration.action.frontend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewChecklistActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTableActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTextActions;
import com.github.saphyra.apphub.integration.core.HeadedSeleniumTest;
import com.github.saphyra.apphub.integration.core.driver.WebDriverMode;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WindowType;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AutoRefreshContentTest extends HeadedSeleniumTest {
    private static final String CATEGORY_TITLE = "category-title";
    private static final String NEW_CATEGORY_TITLE = "new-category-title";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String CONTENT = "content";
    private static final String NEW_CHECKLIST_TITLE = "new-checklist-title";
    private static final String TABLE_TITLE = "table-title";
    private static final String NEW_TABLE_TITLE = "new-table-title";
    private static final String TEXT_TITLE = "text-title";
    private static final String NEW_TEXT_TITLE = "new-text-title";

    @Test(groups = {"fe", "notebook", "headed-only"})
    public void autoRefreshCategory() {
        WebDriver driver = extractDriver(WebDriverMode.HEADED);
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_TITLE);

        NotebookActions.findListItemByTitleValidated(driver, CATEGORY_TITLE)
            .pin(driver);

        driver.switchTo()
            .newWindow(WindowType.TAB);
        driver.navigate().to(UrlFactory.create(getServerPort(), Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, CATEGORY_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Category not found"))
            .edit(getServerPort(), driver);

        EditListItemActions.fillTitle(driver, NEW_CATEGORY_TITLE);
        EditListItemActions.submitForm(driver);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        SleepUtil.sleep(1000);
        assertThat(NotebookActions.getCategoryTree(driver).getChildren().get(0).getTitle()).isEqualTo(NEW_CATEGORY_TITLE);
        assertThat(PinActions.getPinnedItems(driver).get(0).getTitle()).isEqualTo(NEW_CATEGORY_TITLE);
        assertThat(NotebookActions.findListItemByTitle(driver, NEW_CATEGORY_TITLE)).isNotEmpty();
    }

    @Test(groups = {"fe", "notebook", "headed-only"})
    public void autoRefreshSearchResult() {
        WebDriver driver = extractDriver(WebDriverMode.HEADED);
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newCategory(getServerPort(), driver, CATEGORY_TITLE);

        NotebookActions.search(driver, NEW_CATEGORY_TITLE);

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Search result is not loaded.");

        driver.switchTo()
            .newWindow(WindowType.TAB);
        driver.navigate().to(UrlFactory.create(getServerPort(), Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, CATEGORY_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Category not found"))
            .edit(getServerPort(), driver);

        EditListItemActions.fillTitle(driver, NEW_CATEGORY_TITLE);
        EditListItemActions.submitForm(driver);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.findListItemByTitle(driver, NEW_CATEGORY_TITLE).isPresent())
            .assertTrue("Search result is not updated.");
    }

    @Test(groups = {"fe", "notebook", "headed-only"})
    public void autoRefreshChecklist() {
        WebDriver driver = extractDriver(WebDriverMode.HEADED);
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newChecklist(getServerPort(), driver, CHECKLIST_TITLE, List.of(new BiWrapper<>(CONTENT, false)));

        NotebookActions.findListItemByTitleValidated(driver, CHECKLIST_TITLE)
            .open();

        driver.switchTo()
            .newWindow(WindowType.TAB);
        driver.navigate().to(UrlFactory.create(getServerPort(), Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, CHECKLIST_TITLE))
            .orElseThrow(() -> new RuntimeException("Checklist not found"))
            .open();

        ViewChecklistActions.enableEditing(driver);
        ViewChecklistActions.fillTitle(driver, NEW_CHECKLIST_TITLE);
        ViewChecklistActions.saveChanges(driver);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        AwaitilityWrapper.createDefault()
            .until(() -> ViewChecklistActions.getTitle(driver).equals(NEW_CHECKLIST_TITLE))
            .assertTrue("Checklist is not refreshed.");
    }

    @Test(groups = {"fe", "notebook", "headed-only"})
    public void autoRefreshTable() {
        WebDriver driver = extractDriver(WebDriverMode.HEADED);
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newTable(getServerPort(), driver, TABLE_TITLE, List.of(CONTENT), List.of(List.of(CONTENT)));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, TABLE_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Table not found."))
            .open();

        driver.switchTo()
            .newWindow(WindowType.TAB);
        driver.navigate().to(UrlFactory.create(getServerPort(), Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, TABLE_TITLE))
            .orElseThrow(() -> new RuntimeException("Checklist not found"))
            .open();

        ViewTableActions.enableEditing(driver);
        ViewTableActions.fillTitle(driver, NEW_TABLE_TITLE);
        ViewTableActions.saveChanges(driver);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        AwaitilityWrapper.createDefault()
            .until(() -> ViewTableActions.getTitle(driver).equals(NEW_TABLE_TITLE))
            .assertTrue("Table is not refreshed.");
    }

    @Test(groups = {"fe", "notebook", "headed-only"})
    public void autoRefreshText() {
        WebDriver driver = extractDriver(WebDriverMode.HEADED);
        Navigation.toIndexPage(getServerPort(), driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(getServerPort(), driver, ModuleLocation.NOTEBOOK);

        NotebookUtils.newText(getServerPort(), driver, TEXT_TITLE, CONTENT);

        NotebookActions.findListItemByTitleValidated(driver, TEXT_TITLE)
            .open();

        driver.switchTo()
            .newWindow(WindowType.TAB);
        driver.navigate().to(UrlFactory.create(getServerPort(), Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, TEXT_TITLE))
            .orElseThrow(() -> new RuntimeException("Checklist not found"))
            .open();

        ViewTextActions.enableEditing(driver);
        ViewTextActions.fillTitle(driver, NEW_TEXT_TITLE);
        ViewTextActions.saveChanges(driver);

        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(1));
        driver.close();
        driver.switchTo().window(new ArrayList<>(driver.getWindowHandles()).get(0));

        AwaitilityWrapper.createDefault()
            .until(() -> ViewTextActions.getTitle(driver).equals(NEW_TEXT_TITLE))
            .assertTrue("Text is not refreshed.");
    }
}
