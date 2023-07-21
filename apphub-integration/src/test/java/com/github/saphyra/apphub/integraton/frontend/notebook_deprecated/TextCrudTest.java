package com.github.saphyra.apphub.integraton.frontend.notebook_deprecated;

import com.github.saphyra.apphub.integration.core.SeleniumTest;
import com.github.saphyra.apphub.integration.action.frontend.index.IndexPageActions;
import com.github.saphyra.apphub.integration.action.frontend.modules.ModulesPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.CategoryActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.DetailedListActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.NotebookPageActions;
import com.github.saphyra.apphub.integration.action.frontend.notebook_deprecated.TextActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Navigation;
import com.github.saphyra.apphub.integration.framework.NotificationUtil;
import com.github.saphyra.apphub.integration.structure.api.modules.ModuleLocation;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemDetailsItem;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
@Ignore
public class TextCrudTest extends SeleniumTest {
    private static final String CATEGORY_TITLE_1 = "category-title-1";
    private static final String CATEGORY_TITLE_2 = "category-title-2";
    private static final String TEXT_TITLE = "text-title";
    private static final String TEXT_CONTENT = "text-content";
    private static final String NEW_TEXT_TITLE = "new-text-title";
    private static final String NEW_TEXT_CONTENT = "new-text-content";

    @Test
    public void textCrud() {
        WebDriver driver = extractDriver();
        Navigation.toIndexPage(driver);
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(driver, userData);

        ModulesPageActions.openModule(driver, ModuleLocation.NOTEBOOK);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_1);
        CategoryActions.createCategory(driver, CATEGORY_TITLE_2);

        //Create - Empty title
        TextActions.openCreateTextWindow(driver);
        TextActions.submitCreateTextForm(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(TextActions.isCreateTextWindowDisplayed(driver)).isTrue();

        //Create
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

        NotificationUtil.clearNotifications(driver);

        //Edit - Empty title
        TextActions.openText(driver, TEXT_TITLE);
        TextActions.enableEditing(driver);
        TextActions.editTitle(driver, "");
        TextActions.saveChanges(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");
        assertThat(TextActions.isEditingEnabled(driver)).isTrue();

        //Edit - Discard
        TextActions.editTitle(driver, NEW_TEXT_TITLE);
        TextActions.editContent(driver, NEW_TEXT_CONTENT);
        TextActions.discardChanges(driver);
        AwaitilityWrapper.createDefault()
            .until(() -> !TextActions.isEditingEnabled(driver))
            .assertTrue();
        assertThat(TextActions.getTitle(driver)).isEqualTo(TEXT_TITLE);
        assertThat(TextActions.getContent(driver)).isEqualTo(TEXT_CONTENT);

        //Edit
        TextActions.enableEditing(driver);
        TextActions.editTitle(driver, NEW_TEXT_TITLE);
        TextActions.editContent(driver, NEW_TEXT_CONTENT);
        TextActions.saveChanges(driver);
        NotificationUtil.verifySuccessNotification(driver, "Szöveg elmentve.");
        AwaitilityWrapper.createDefault()
            .until(() -> !TextActions.isEditingEnabled(driver))
            .assertTrue("Editing remained enabled.");
        TextActions.closeView(driver);
        TextActions.openText(driver, NEW_TEXT_TITLE);
        assertThat(TextActions.getTitle(driver)).isEqualTo(NEW_TEXT_TITLE);
        assertThat(TextActions.getContent(driver)).isEqualTo(NEW_TEXT_CONTENT);

        NotificationUtil.clearNotifications(driver);
        TextActions.closeView(driver);

        //Edit as listItem - Empty title
        DetailedListActions.findDetailedItem(driver, NEW_TEXT_TITLE)
            .edit(driver);
        NotebookPageActions.fillEditListItemDialog(driver, "", null, 0);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifyErrorNotification(driver, "A cím nem lehet üres.");

        //Edit as listItem
        NotebookPageActions.fillEditListItemDialog(driver, TEXT_TITLE, null, 1, CATEGORY_TITLE_2);
        NotebookPageActions.submitEditListItemDialog(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem elmentve.");
        NotebookPageActions.verifyEditListItemDialogClosed(driver);
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
        DetailedListActions.up(driver);
        CategoryActions.openCategory(driver, CATEGORY_TITLE_2);
        DetailedListActions.findDetailedItem(driver, TEXT_TITLE);

        //Delete
        DetailedListActions.findDetailedItem(driver, TEXT_TITLE)
            .delete(driver);
        NotificationUtil.verifySuccessNotification(driver, "Elem törölve.");
        assertThat(DetailedListActions.getDetailedListItems(driver)).isEmpty();
    }
}
