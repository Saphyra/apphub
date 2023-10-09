package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleyRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

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
    private static final String ONLY_TITLE_TITLE = "only-title-title";

    @Test(groups = {"be", "notebook"})
    public void cloneListItem() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        listItemNotFound(accessTokenId);
        clone(accessTokenId);
    }

    private static void listItemNotFound(UUID accessTokenId) {
        Response response = NotebookActions.getCloneListItemResponse(accessTokenId, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(response, 404, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    private void clone(UUID accessTokenId) {
        UUID rootId = NotebookActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(ROOT_TITLE).build());
        UUID parentId = NotebookActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).parent(rootId).build());
        UUID childCategoryId = NotebookActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(CHILD_CATEGORY_TITLE).parent(parentId).build());

        NotebookActions.createLink(accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).parent(childCategoryId).url(LINK_URL).build());
        NotebookActions.createText(accessTokenId, CreateTextRequest.builder().title(TEXT_TITLE).content(TEXT_CONTENT).parent(parentId).build());
        NotebookActions.createOnlyTitle(accessTokenId, CreateOnlyTitleyRequest.builder().title(ONLY_TITLE_TITLE).parent(parentId).build());
        NotebookActions.createChecklist(

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

            accessTokenId,
            CreateTableRequest.builder()
                .title(TABLE_TITLE)
                .parent(parentId)
                .columnNames(Arrays.asList(TABLE_COLUMN_NAME))
                .columns(Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)))
                .build()
        );

        NotebookActions.createChecklistTable(

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

        NotebookActions.archive(accessTokenId, parentId, true);
        NotebookActions.pin(accessTokenId, parentId, true);

        Response cloneResponse = NotebookActions.getCloneListItemResponse(accessTokenId, parentId);

        assertThat(cloneResponse.getStatusCode()).isEqualTo(200);

        ChildrenOfCategoryResponse rootItems = NotebookActions.getChildrenOfCategory(accessTokenId, rootId);
        assertThat(rootItems.getChildren()).hasSize(2);
        assertThat(rootItems.getChildren().stream().allMatch(notebookView -> notebookView.getTitle().equals(PARENT_TITLE))).isTrue();

        NotebookView clonedItem = rootItems.getChildren()
            .stream()
            .filter(notebookView -> !notebookView.getId().equals(parentId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Clone not found"));

        assertThat(clonedItem.isArchived()).isTrue();
        assertThat(clonedItem.isPinned()).isTrue();

        UUID clonedParentId = clonedItem.getId();

        ChildrenOfCategoryResponse clonedParentItems = NotebookActions.getChildrenOfCategory(accessTokenId, clonedParentId);
        assertThat(clonedParentItems.getChildren()).hasSize(6);

        UUID clonedChildCategoryId = findByTitle(CHILD_CATEGORY_TITLE, clonedParentItems.getChildren()).getId();
        ChildrenOfCategoryResponse clonedChildCategoryItems = NotebookActions.getChildrenOfCategory(accessTokenId, clonedChildCategoryId);
        assertThat(clonedChildCategoryItems.getChildren()).hasSize(1);

        NotebookView linkItem = findByTitle(LINK_TITLE, clonedChildCategoryItems.getChildren());
        assertThat(linkItem.getType()).isEqualTo(ListItemType.LINK.name());
        assertThat(linkItem.getValue()).isEqualTo(LINK_URL);

        NotebookView textItem = findByTitle(TEXT_TITLE, clonedParentItems.getChildren());
        assertThat(textItem.getType()).isEqualTo(ListItemType.TEXT.name());
        String textContent = NotebookActions.getText(accessTokenId, textItem.getId())
            .getContent();
        assertThat(textContent).isEqualTo(TEXT_CONTENT);

        NotebookView onlyTitleItem = findByTitle(ONLY_TITLE_TITLE, clonedParentItems.getChildren());
        assertThat(onlyTitleItem.getType()).isEqualTo(ListItemType.ONLY_TITLE.name());

        NotebookView checklistItem = findByTitle(CHECKLIST_TITLE, clonedParentItems.getChildren());
        assertThat(checklistItem.getType()).isEqualTo(ListItemType.CHECKLIST.name());
        ChecklistResponse checklistData = NotebookActions.getChecklist(accessTokenId, checklistItem.getId());
        assertThat(checklistData.getNodes()).hasSize(1);
        assertThat(checklistData.getNodes().get(0).getOrder()).isEqualTo(0);
        assertThat(checklistData.getNodes().get(0).getContent()).isEqualTo(CHECKLIST_ITEM_CONTENT);
        assertThat(checklistData.getNodes().get(0).getChecked()).isTrue();

        NotebookView tableItem = findByTitle(TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(tableItem.getType()).isEqualTo(ListItemType.TABLE.name());
        TableResponse tableData = NotebookActions.getTable(accessTokenId, tableItem.getId());
        assertThat(tableData.getTableHeads()).hasSize(1);
        assertThat(tableData.getTableHeads().get(0).getContent()).isEqualTo(TABLE_COLUMN_NAME);
        assertThat(tableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns()).hasSize(1);
        assertThat(tableData.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableData.getTableColumns().get(0).getContent()).isEqualTo(TABLE_COLUMN_VALUE);

        NotebookView checklistTableItem = findByTitle(CHECKLIST_TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(checklistTableItem.getType()).isEqualTo(ListItemType.CHECKLIST_TABLE.name());
        ChecklistTableResponse checklistTableData = NotebookActions.getChecklistTable(accessTokenId, checklistTableItem.getId());
        assertThat(checklistTableData.getTableHeads()).hasSize(1);
        assertThat(checklistTableData.getTableHeads().get(0).getContent()).isEqualTo(CHECKLIST_TABLE_COLUMN_NAME);
        assertThat(checklistTableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableData.getTableColumns()).hasSize(1);
        assertThat(checklistTableData.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableData.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(checklistTableData.getTableColumns().get(0).getContent()).isEqualTo(CHECKLIST_TABLE_COLUMN_VALUE);
        assertThat(checklistTableData.getRowStatus().get(0).getChecked()).isTrue();
    }

    private NotebookView findByTitle(String title, List<NotebookView> views) {
        return views.stream()
            .filter(notebookView -> notebookView.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("NotebookView not found with title " + title));
    }
}