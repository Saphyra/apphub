package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.ErrorResponse;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.structure.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.notebook.TableResponse;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class CloneListItemTest extends BackEndTest {
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
    private static final String CHECKLIST_TABLE_TITLE = "checklist-table-title";
    private static final String CHECKLIST_TABLE_COLUMN_NAME = "checklist-table-column-name";
    private static final String CHECKLIST_TABLE_COLUMN_VALUE = "checklist-table-column-value";

    @Test(dataProvider = "languageDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getCloneListItemResponse(language, accessTokenId, UUID.randomUUID());

        ResponseValidator.verifyListItemNotFound(language, response);
    }

    @Test(dataProvider = "languageDataProvider")
    public void cloneListItem(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //ListItem not found
        Response response = NotebookActions.getCloneListItemResponse(language, accessTokenId, UUID.randomUUID());
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));

        //Clone
        UUID rootId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(ROOT_TITLE).build());
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).parent(rootId).build());
        UUID childCategoryId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(CHILD_CATEGORY_TITLE).parent(parentId).build());

        NotebookActions.createLink(language, accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).parent(childCategoryId).url(LINK_URL).build());
        NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TEXT_TITLE).content(TEXT_CONTENT).parent(parentId).build());
        NotebookActions.createChecklist(
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

        NotebookActions.createChecklistTable(
            language,
            accessTokenId,
            CreateChecklistTableRequest.builder()
                .title(CHECKLIST_TABLE_TITLE)
                .parent(parentId)
                .columnNames(Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME))
                .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder()
                    .checked(true)
                    .columns(Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))
                    .build()))
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
        assertThat(clonedParentItems.getChildren()).hasSize(5);

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

        NotebookView checklistTableItem = findByTitle(CHECKLIST_TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(checklistTableItem.getType()).isEqualTo(ListItemType.CHECKLIST_TABLE.name());
        ChecklistTableResponse checklistTableData = NotebookActions.getChecklistTable(language, accessTokenId, checklistTableItem.getId());
        assertThat(checklistTableData.getTableHeads()).hasSize(1);
        assertThat(checklistTableData.getTableHeads().get(0).getContent()).isEqualTo(CHECKLIST_TABLE_COLUMN_NAME);
        assertThat(checklistTableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableData.getTableColumns()).hasSize(1);
        assertThat(checklistTableData.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableData.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(checklistTableData.getTableColumns().get(0).getContent()).isEqualTo(CHECKLIST_TABLE_COLUMN_VALUE);
        assertThat(checklistTableData.getRowStatus().get(0)).isTrue();
    }

    private NotebookView findByTitle(String title, List<NotebookView> views) {
        return views.stream()
            .filter(notebookView -> notebookView.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("NotebookView not found with title " + title));
    }
}
