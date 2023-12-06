package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.NotebookNewListItemActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.new_list_item.NewTextActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook.view.ViewTextActions;
import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.ToastMessageUtil;
import com.github.saphyra.apphub.integration.framework.WebElementUtils;
import com.github.saphyra.apphub.integration.localization.LocalizedText;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class TextCrudTest extends SeleniumTest {
    private static final String TEXT_TITLE = "text";
    private static final String TEXT_CONTENT = "content";
    private static final String NEW_TEXT_TITLE = "new-text";
    private static final String NEW_TEXT_CONTENT = "new-content";

    @Test(groups = {"fe", "notebook"})
    public void textCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        NotebookActions.newListItem(driver);
        NotebookNewListItemActions.selectListItemType(driver, ListItemType.TEXT);

        create_emptyTitle(driver);
        create(driver);
        edit_emptyTitle(driver);
        edit_discard(driver);
        edit(driver);
        delete(driver);
    }

    private static void create_emptyTitle(WebDriver driver) {
        NewTextActions.fillTitle(driver, " ");
        NewTextActions.submit(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);
    }

    private static void create(WebDriver driver) {
        NewTextActions.fillTitle(driver, TEXT_TITLE);
        NewTextActions.fillContent(driver, TEXT_CONTENT);

        NewTextActions.submit(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> driver.getCurrentUrl().endsWith(Endpoints.NOTEBOOK_PAGE));

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, TEXT_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Text not found"))
            .open(() -> WebElementUtils.getIfPresent(() -> driver.findElement(By.id("notebook-content-text"))).isPresent());

        assertThat(ViewTextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);
        assertThat(ViewTextActions.getTitle(driver)).isEqualTo(TEXT_TITLE);
    }

    private static void edit_emptyTitle(WebDriver driver) {
        ViewTextActions.enableEditing(driver);

        ViewTextActions.setTitle(driver, " ");
        ViewTextActions.saveChanges(driver);

        ToastMessageUtil.verifyErrorToast(driver, LocalizedText.NOTEBOOK_TITLE_MUST_NOT_BE_BLANK);
    }

    private static void edit_discard(WebDriver driver) {
        ViewTextActions.setTitle(driver, NEW_TEXT_TITLE);
        ViewTextActions.setContent(driver, NEW_TEXT_CONTENT);

        ViewTextActions.discardChanges(driver);

        assertThat(ViewTextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);
        assertThat(ViewTextActions.getTitle(driver)).isEqualTo(TEXT_TITLE);
    }

    private static void edit(WebDriver driver) {
        ViewTextActions.enableEditing(driver);
        ViewTextActions.setTitle(driver, NEW_TEXT_TITLE);
        ViewTextActions.setContent(driver, NEW_TEXT_CONTENT);

        ViewTextActions.saveChanges(driver);

        assertThat(ViewTextActions.getContent(driver)).isEqualTo(NEW_TEXT_CONTENT);
        assertThat(ViewTextActions.getTitle(driver)).isEqualTo(NEW_TEXT_TITLE);
    }

    private static void delete(WebDriver driver) {
        ViewTextActions.close(driver);

        AwaitilityWrapper.getOptionalWithWait(() -> NotebookActions.findListItemByTitle(driver, NEW_TEXT_TITLE), Optional::isPresent)
            .orElseThrow(() -> new RuntimeException("Text with the new title not found"))
            .delete(driver);

        driver.findElement(By.id("notebook-content-category-content-delete-list-item-button"))
            .click();

        AwaitilityWrapper.createDefault()
            .until(() -> NotebookActions.getListItems(driver).isEmpty())
            .assertTrue("Text is not deleted.");
    }
}
