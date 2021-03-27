package com.github.saphyra.apphub.integration.frontend.notebook;

import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.frontend.SeleniumTest;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.frontend.framework.Navigation;
import com.github.saphyra.apphub.integration.frontend.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.frontend.model.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.frontend.model.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.frontend.service.index.IndexPageActions;
import com.github.saphyra.apphub.integration.frontend.service.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.frontend.service.notebook.*;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TextCrudTest extends SeleniumTest {
    private static final String CATEGORY_TITLE_1 = "category-title-1";
    private static final String CATEGORY_TITLE_2 = "category-title-2";
    private static final String TEXT_TITLE = "text-title";
    private static final String TEXT_CONTENT = "text-content";
    private static final String NEW_TEXT_TITLE = "new-text-title";
    private static final String NEW_TEXT_CONTENT = "new-text-content";

    @Test
    public void createText_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TextActions.openCreateTextWindow(driver);
        TextActions.submitCreateTextForm(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(TextActions.isCreateTextWindowDisplayed(driver)).isTrue();
    }

    @Test
    public void createText() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);

        TextActions.openCreateTextWindow(driver);
        TextActions.fillNewTextTitle(driver, TEXT_TITLE);
        TextActions.fillNewTextContent(driver, TEXT_CONTENT);
        TextActions.selectCategoryForNewText(driver, CATEGORY_TITLE_1);
        TextActions.submitCreateTextForm(driver);

        NotificationUtil.verifySuccessNotification(driver, "Szöveg elmentve.");

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        List<ListItemDetailsItem> detailedListItems = DetailedListActions.getDetailedListItems(driver);
        assertThat(detailedListItems).hasSize(1);
        ListItemDetailsItem textItem = detailedListItems.get(0);
        assertThat(textItem.getTitle()).isEqualTo(TEXT_TITLE);
        assertThat(textItem.getType()).isEqualTo(ListItemType.TEXT);
    }

    @Test
    public void editText_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);

        TextActions.openText(driver, TEXT_TITLE);
        TextActions.allowEditing(driver);
        TextActions.editTitle(driver, "");
        TextActions.saveChanges(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(TextActions.isEditingEnabled(driver)).isTrue();
    }

    @Test
    public void editText_discard() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);

        TextActions.openText(driver, TEXT_TITLE);
        TextActions.allowEditing(driver);

        TextActions.editTitle(driver, NEW_TEXT_TITLE);
        TextActions.editContent(driver, NEW_TEXT_CONTENT);

        TextActions.discardChanges(driver);

        AwaitilityWrapper.createDefault()
            .until(() -> !TextActions.isEditingEnabled(driver))
            .assertTrue();
        assertThat(TextActions.getTitle(driver)).isEqualTo(TEXT_TITLE);
        assertThat(TextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);
    }

    @Test
    public void editText() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);

        TextActions.openText(driver, TEXT_TITLE);
        TextActions.allowEditing(driver);

        TextActions.editTitle(driver, NEW_TEXT_TITLE);
        TextActions.editContent(driver, NEW_TEXT_CONTENT);

        TextActions.saveChanges(driver);

        NotificationUtil.verifySuccessNotification(driver, "Szöveg elmentve.");
        assertThat(TextActions.isEditingEnabled(driver)).isFalse();

        TextActions.closeView(driver);

        TextActions.openText(driver, NEW_TEXT_TITLE);

        assertThat(TextActions.getTitle(driver)).isEqualTo(NEW_TEXT_TITLE);
        assertThat(TextActions.getContent(driver)).isEqualTo(NEW_TEXT_CONTENT);
    }

    @Test
    public void deleteText() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);

        DetailedListActions.findDetailedItem(driver, TEXT_TITLE)
            .delete(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }

    @Test
    public void editTextListItem_emptyTitle() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT);

        DetailedListActions.findDetailedItem(driver, TEXT_TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
    }

    @Test
    public void editTextListItem() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);

        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        TextActions.createText(driver, TEXT_TITLE, TEXT_CONTENT, CATEGORY_TITLE_1);

        CategoryActions.openCategory(driver, CATEGORY_TITLE_1);

        DetailedListActions.findDetailedItem(driver, TEXT_TITLE)
            .edit(driver);

        NotebookPageActions.fillEditListItemDialog(driver, NEW_TEXT_TITLE, null, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);

        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);

        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();

        DetailedListActions.up(driver);

        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);

        DetailedListActions.findDetailedItem(driver, NEW_TEXT_TITLE);
    }
}
