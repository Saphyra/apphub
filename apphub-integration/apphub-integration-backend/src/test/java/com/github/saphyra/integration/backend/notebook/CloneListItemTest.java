package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class CloneListItemTest extends TestBase {
    private static final String ROOT_TITLE = "root-title";
    private static final String PARENT_TITLE = "parent-title";
    private static final String CHILD_CATEGORY_TITLE = "child-category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = "link-url";
    private static final String TEXT_CONTENT = "text-content";
    private static final String CHECKLIST_ITEM_CONTENT = "checklist-item-content";
    private static final String TABLE_COLUMN_NAME = "table-column-name";
    private static final String TABLE_COLUMN_VALUE = "table-column-value";
    private static final String TEXT_TITLE = "text-title";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String TABLE_TITLE = "table-title";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getCloneListItemResponse(language, accessTokenId, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void cloneListItem() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID rootId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(ROOT_TITLE).build());
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).parent(rootId).build());
        UUID childCategoryId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(CHILD_CATEGORY_TITLE).parent(parentId).build());

        NotebookActions.createLink(language, accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).parent(childCategoryId).url(LINK_URL).build());
        NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TEXT_TITLE).content(TEXT_CONTENT).parent(parentId).build());
        NotebookActions.createChecklistItem(
            language,
            accessTokenId,
            CreateChecklistItemRequest.builder()
                .title(CHECKLIST_TITLE)
                .parent(parentId)
                .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                    .order(0)
                    .checked(true)
                    .content(CHECKLIST_ITEM_CONTENT)
                    .build()))
                .build()
        );
        NotebookActions.createTable(
            language,
            accessTokenId,
            CreateTableRequest.builder()
                .title(TABLE_TITLE)
                .parent(parentId)
                .columnNames(Arrays.asList(TABLE_COLUMN_NAME))
                .columns(Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)))
                .build()
        );

        Response cloneResponse = NotebookActions.getCloneListItemResponse(language, accessTokenId, parentId);

        assertThat(cloneResponse.getStatusCode()).isEqualTo(200);

        ChildrenOfCategoryResponse rootItems = NotebookActions.getChildrenOfCategory(language, accessTokenId, rootId);
        assertThat(rootItems.getChildren()).hasSize(2);
        assertThat(rootItems.getChildren().stream().allMatch(notebookView -> notebookView.getTitle().equals(PARENT_TITLE))).isTrue();

        UUID clonedParentId = rootItems.getChildren()
            .stream()
            .filter(notebookView -> !notebookView.getId().equals(parentId))
            .findFirst()
            .map(NotebookView::getId)
            .orElseThrow(() -> new RuntimeException("Clone not found"));

        ChildrenOfCategoryResponse clonedParentItems = NotebookActions.getChildrenOfCategory(language, accessTokenId, clonedParentId);
        assertThat(clonedParentItems.getChildren()).hasSize(4);

        UUID clonedChildCategoryId = findByTitle(CHILD_CATEGORY_TITLE, clonedParentItems.getChildren()).getId();
        ChildrenOfCategoryResponse clonedChildCategoryItems = NotebookActions.getChildrenOfCategory(language, accessTokenId, clonedChildCategoryId);
        assertThat(clonedChildCategoryItems.getChildren()).hasSize(1);

        NotebookView linkItem = findByTitle(LINK_TITLE, clonedChildCategoryItems.getChildren());
        assertThat(linkItem.getType()).isEqualTo(ListItemType.LINK.name());
        assertThat(linkItem.getValue()).isEqualTo(LINK_URL);

        NotebookView textItem = findByTitle(TEXT_TITLE, clonedParentItems.getChildren());
        assertThat(textItem.getType()).isEqualTo(ListItemType.TEXT.name());
        String textContent = NotebookActions.getText(language, accessTokenId, textItem.getId())
            .getContent();
        assertThat(textContent).isEqualTo(TEXT_CONTENT);

        NotebookView checklistItem = findByTitle(CHECKLIST_TITLE, clonedParentItems.getChildren());
        assertThat(checklistItem.getType()).isEqualTo(ListItemType.CHECKLIST.name());
        ChecklistResponse checklistData = NotebookActions.getChecklist(language, accessTokenId, checklistItem.getId());
        assertThat(checklistData.getNodes()).hasSize(1);
        assertThat(checklistData.getNodes().get(0).getOrder()).isEqualTo(0);
        assertThat(checklistData.getNodes().get(0).getContent()).isEqualTo(CHECKLIST_ITEM_CONTENT);
        assertThat(checklistData.getNodes().get(0).getChecked()).isTrue();

        NotebookView tableItem = findByTitle(TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(tableItem.getType()).isEqualTo(ListItemType.TABLE.name());
        TableResponse tableData = NotebookActions.getTable(language, accessTokenId, tableItem.getId());
        assertThat(tableData.getTableHeads()).hasSize(1);
        assertThat(tableData.getTableHeads().get(0).getContent()).isEqualTo(TABLE_COLUMN_NAME);
        assertThat(tableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns()).hasSize(1);
        assertThat(tableData.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns().get(0).getContent()).isEqualTo(TABLE_COLUMN_VALUE);
    }

    private NotebookView findByTitle(String title, List<NotebookView> views) {
        return views.stream()
            .filter(notebookView -> notebookView.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("NotebookView not found with title " + title));
    }
}
