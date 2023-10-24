package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.OnlyTitleActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleyRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableColumnModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
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
        Response response = ListItemActions.getCloneListItemResponse(accessTokenId, UUID.randomUUID());
        ResponseValidator.verifyErrorResponse(response, 404, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    private void clone(UUID accessTokenId) {
        UUID rootId = CategoryActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(ROOT_TITLE).build());
        UUID parentId = CategoryActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).parent(rootId).build());
        UUID childCategoryId = CategoryActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(CHILD_CATEGORY_TITLE).parent(parentId).build());

        LinkActions.createLink(accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).parent(childCategoryId).url(LINK_URL).build());
        TextActions.createText(accessTokenId, CreateTextRequest.builder().title(TEXT_TITLE).content(TEXT_CONTENT).parent(parentId).build());
        OnlyTitleActions.createOnlyTitle(accessTokenId, CreateOnlyTitleyRequest.builder().title(ONLY_TITLE_TITLE).parent(parentId).build());
        ChecklistActions.createChecklist(
            accessTokenId,
            CreateChecklistRequest.builder()
                .title(CHECKLIST_TITLE)
                .parent(parentId)
                .items(Arrays.asList(ChecklistItemModel.builder()
                    .index(0)
                    .checked(true)
                    .content(CHECKLIST_ITEM_CONTENT)
                    .build()))
                .build()
        );
        TableActions.createTable(
            accessTokenId,
            CreateTableRequest.builder()
                .title(TABLE_TITLE)
                .listItemType(ListItemType.TABLE)
                .parent(parentId)
                .tableHeads(List.of(TableHeadModel.builder()
                    .columnIndex(0)
                    .content(TABLE_COLUMN_NAME)
                    .build()))
                .rows(List.of(TableRowModel.builder()
                    .rowIndex(0)
                    .columns(List.of(TableColumnModel.builder()
                        .columnType(ColumnType.TEXT)
                        .columnIndex(0)
                        .data(TABLE_COLUMN_VALUE)
                        .build()))
                    .build()))
                .build()
        );

        TableActions.createTable(
            accessTokenId,
            CreateTableRequest.builder()
                .title(CHECKLIST_TABLE_TITLE)
                .listItemType(ListItemType.CHECKLIST_TABLE)
                .parent(parentId)
                .tableHeads(List.of(TableHeadModel.builder()
                    .columnIndex(0)
                    .content(CHECKLIST_TABLE_COLUMN_NAME)
                    .build()))
                .rows(List.of(TableRowModel.builder()
                    .rowIndex(0)
                        .checked(true)
                    .columns(List.of(TableColumnModel.builder()
                        .columnType(ColumnType.TEXT)
                        .columnIndex(0)
                        .data(CHECKLIST_TABLE_COLUMN_VALUE)
                        .build()))
                    .build()))
                .build()
        );

        ListItemActions.archive(accessTokenId, parentId, true);
        ListItemActions.pin(accessTokenId, parentId, true);

        Response cloneResponse = ListItemActions.getCloneListItemResponse(accessTokenId, parentId);

        assertThat(cloneResponse.getStatusCode()).isEqualTo(200);

        ChildrenOfCategoryResponse rootItems = CategoryActions.getChildrenOfCategory(accessTokenId, rootId);
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

        ChildrenOfCategoryResponse clonedParentItems = CategoryActions.getChildrenOfCategory(accessTokenId, clonedParentId);
        assertThat(clonedParentItems.getChildren()).hasSize(6);

        UUID clonedChildCategoryId = findByTitle(CHILD_CATEGORY_TITLE, clonedParentItems.getChildren()).getId();
        ChildrenOfCategoryResponse clonedChildCategoryItems = CategoryActions.getChildrenOfCategory(accessTokenId, clonedChildCategoryId);
        assertThat(clonedChildCategoryItems.getChildren()).hasSize(1);

        NotebookView linkItem = findByTitle(LINK_TITLE, clonedChildCategoryItems.getChildren());
        assertThat(linkItem.getType()).isEqualTo(ListItemType.LINK.name());
        assertThat(linkItem.getValue()).isEqualTo(LINK_URL);

        NotebookView textItem = findByTitle(TEXT_TITLE, clonedParentItems.getChildren());
        assertThat(textItem.getType()).isEqualTo(ListItemType.TEXT.name());
        String textContent = TextActions.getText(accessTokenId, textItem.getId())
            .getContent();
        assertThat(textContent).isEqualTo(TEXT_CONTENT);

        NotebookView onlyTitleItem = findByTitle(ONLY_TITLE_TITLE, clonedParentItems.getChildren());
        assertThat(onlyTitleItem.getType()).isEqualTo(ListItemType.ONLY_TITLE.name());

        NotebookView checklistItem = findByTitle(CHECKLIST_TITLE, clonedParentItems.getChildren());
        assertThat(checklistItem.getType()).isEqualTo(ListItemType.CHECKLIST.name());
        ChecklistResponse checklistData = ChecklistActions.getChecklist(accessTokenId, checklistItem.getId());
        assertThat(checklistData.getItems()).hasSize(1);
        assertThat(checklistData.getItems().get(0).getIndex()).isEqualTo(0);
        assertThat(checklistData.getItems().get(0).getContent()).isEqualTo(CHECKLIST_ITEM_CONTENT);
        assertThat(checklistData.getItems().get(0).getChecked()).isTrue();

        NotebookView tableItem = findByTitle(TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(tableItem.getType()).isEqualTo(ListItemType.TABLE.name());
        TableResponse tableData = TableActions.getTable(accessTokenId, tableItem.getId());
        assertThat(tableData.getTableHeads()).hasSize(1);
        assertThat(tableData.getTableHeads().get(0).getContent()).isEqualTo(TABLE_COLUMN_NAME);
        assertThat(tableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableData.getRows()).hasSize(1);
        assertThat(tableData.getRows().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableData.getRows().get(0).getColumns()).hasSize(1);
        assertThat(tableData.getRows().get(0).getColumns().get(0).getData()).isEqualTo(TABLE_COLUMN_VALUE);
        assertThat(tableData.getRows().get(0).getColumns().get(0).getColumnIndex()).isEqualTo(0);

        NotebookView checklistTableItem = findByTitle(CHECKLIST_TABLE_TITLE, clonedParentItems.getChildren());
        assertThat(checklistTableItem.getType()).isEqualTo(ListItemType.CHECKLIST_TABLE.name());
        TableResponse checklistTableData = TableActions.getTable(accessTokenId, checklistTableItem.getId());
        assertThat(checklistTableData.getTableHeads()).hasSize(1);
        assertThat(checklistTableData.getTableHeads().get(0).getContent()).isEqualTo(CHECKLIST_TABLE_COLUMN_NAME);
        assertThat(checklistTableData.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableData.getRows()).hasSize(1);
        assertThat(checklistTableData.getRows().get(0).getRowIndex()).isEqualTo(0);
        assertThat(checklistTableData.getRows().get(0).getChecked()).isTrue();
        assertThat(checklistTableData.getRows().get(0).getColumns()).hasSize(1);
        assertThat(checklistTableData.getRows().get(0).getColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableData.getRows().get(0).getColumns().get(0).getData()).isEqualTo(CHECKLIST_TABLE_COLUMN_VALUE);
    }

    private NotebookView findByTitle(String title, List<NotebookView> views) {
        return views.stream()
            .filter(notebookView -> notebookView.getTitle().equals(title))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("NotebookView not found with title " + title));
    }
}
