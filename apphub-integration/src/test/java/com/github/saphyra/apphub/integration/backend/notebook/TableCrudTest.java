package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableColumnModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyListItemNotFound;
import static org.assertj.core.api.Assertions.assertThat;

//TODO split methods
public class TableCrudTest extends BackEndTest {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TABLE_TITLE = "table-title";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test(groups = {"be", "notebook"})
    public void tableCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        //Create - Blank title
        CreateTableRequest create_blankTitleRequest = CreateTableRequest.builder()
            .title(" ")
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Null ListItemType
        CreateTableRequest create_nullListItemTypeRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(null)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response create_nullListItemTypeResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_nullListItemTypeRequest);
        verifyInvalidParam(create_nullListItemTypeResponse, "listItemType", "must not be null");

        //Create - Parent not found
        CreateTableRequest create_parentNotFoundRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .parent(UUID.randomUUID())
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(accessTokenId, CreateTextRequest.builder().title("title").content("").build());
        CreateTableRequest create_parentNotCategoryRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .parent(notCategoryParentId)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //Create - Blank column name
        CreateTableRequest create_blankColumnNameRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(" ")
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response create_blankColumnNameResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_blankColumnNameRequest);
        verifyInvalidParam(create_blankColumnNameResponse, "tableHead.content", "must not be null or blank");

        //Create - null rows
        //TODO

        //Create - null rowIndex
        //TODO

        //Create - null columns
        //TODO

        //Create - Incorrect column amount
        CreateTableRequest create_incorrectColumnAmountRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(
                    TableColumnModel.builder()
                        .columnIndex(0)
                        .columnType(ColumnType.TEXT)
                        .data(COLUMN_VALUE)
                        .build(),
                    TableColumnModel.builder()
                        .columnIndex(1)
                        .columnType(ColumnType.TEXT)
                        .data(COLUMN_VALUE)
                        .build()
                ))
                .build()))
            .build();
        Response create_incorrectColumnAmountResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_incorrectColumnAmountRequest);
        verifyInvalidParam(create_incorrectColumnAmountResponse, "row.columns", "item count mismatch");

        //Create - Null column value
        CreateTableRequest create_nullColumnValueRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(null)
                    .build()))
                .build()))
            .build();
        Response create_nullColumnValueResponse = NotebookActions.getCreateTableResponse(accessTokenId, create_nullColumnValueRequest);
        verifyInvalidParam(create_nullColumnValueResponse, "textValue", "must not be null");

        //Create - Null columnType
        //TODO

        //Create - Null columnIndex
        //TODO

        //Create
        CreateTableRequest createRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        NotebookActions.createTable(accessTokenId, createRequest);
        UUID listItemId = NotebookActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren()
            .stream()
            .filter(notebookView -> notebookView.getTitle().equals(TABLE_TITLE))
            .map(NotebookView::getId)
            .findAny()
            .orElseThrow(() -> new RuntimeException("Table was not created."));
        TableResponse tableResponse = NotebookActions.getTable(accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(COLUMN_VALUE);

        //Get - ListItem not found
        Response get_listItemNotFoundResponse = NotebookActions.getTableResponse(accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(get_listItemNotFoundResponse);

        //Edit - Blank title
        EditTableRequest edit_blankTitleRequest = EditTableRequest.builder()
            .title(" ")
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_blankTitleResponse = NotebookActions.getEditTableResponse(accessTokenId, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");

        //Edit - Blank column name
        EditTableRequest edit_blankColumnNameRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(" ")
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_blankColumnNameResponse = NotebookActions.getEditTableResponse(accessTokenId, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(edit_blankColumnNameResponse, "tableHead.content", "must not be null or blank");

        //Edit - Different column amount
        EditTableRequest edit_differentColumnAmountRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(
                    TableColumnModel.builder()
                        .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                        .columnIndex(0)
                        .columnType(ColumnType.TEXT)
                        .itemType(ItemType.EXISTING)
                        .data(NEW_COLUMN_NAME)
                        .build(),
                    TableColumnModel.builder()
                        .columnIndex(0)
                        .columnType(ColumnType.TEXT)
                        .itemType(ItemType.NEW)
                        .data("asd")
                        .build()
                ))
                .build()
            ))
            .build();
        Response edit_differentColumnAmountResponse = NotebookActions.getEditTableResponse(accessTokenId, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(edit_differentColumnAmountResponse, "row.columns", "item count mismatch");

        //Edit - Null column value
        EditTableRequest edit_nullColumnValueRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(null)
                    .build()))
                .build()
            ))
            .build();
        Response edit_nullColumnValueResponse = NotebookActions.getEditTableResponse(accessTokenId, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(edit_nullColumnValueResponse, "textValue", "must not be null");

        //Edit - ColumnHead not found
        EditTableRequest edit_columnHeadNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(UUID.randomUUID())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_columnHeadNotFoundResponse = NotebookActions.getEditTableResponse(accessTokenId, listItemId, edit_columnHeadNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_columnHeadNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);

        //Edit - TableJoin not found
        EditTableRequest edit_tableJoinNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(UUID.randomUUID())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_tableJoinNotFoundResponse = NotebookActions.getEditTableResponse(accessTokenId, listItemId, edit_tableJoinNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_tableJoinNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);

        //Edit - ListItem not found
        EditTableRequest edit_listItemNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_listItemNotFoundResponse = NotebookActions.getEditTableResponse(accessTokenId, UUID.randomUUID(), edit_listItemNotFoundRequest);
        ResponseValidator.verifyInvalidParam(edit_listItemNotFoundResponse, "tableHead.tableHeadId", "points to different table");

        //Edit - Column deleted
        EditTableRequest edit_columnDeletedRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();
        NotebookActions.editTable(accessTokenId, listItemId, edit_columnDeletedRequest);
        tableResponse = NotebookActions.getTable(accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getRows()).isEmpty();

        //Edit - Column added
        EditTableRequest edit_columnAddedRequest = EditTableRequest.builder()
            .title(TABLE_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .columnIndex(0)
                    .content(COLUMN_NAME)
                    .type(ItemType.NEW)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .itemType(ItemType.NEW)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.NEW)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()
            ))
            .build();
        NotebookActions.editTable(accessTokenId, listItemId, edit_columnAddedRequest);
        tableResponse = NotebookActions.getTable(accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(COLUMN_VALUE);

        //Edit - Column modified
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().get(0).getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().get(0).getColumns().get(0).getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_VALUE)
                    .build()))
                .build()
            ))
            .build();
        NotebookActions.editTable(accessTokenId, listItemId, editTableRequest);
        tableResponse = NotebookActions.getTable(accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(NEW_COLUMN_VALUE);

        NotebookActions.deleteListItem(accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    @Test(groups = {"be", "notebook"})
    public void checklistTableTest() {
        //TODO
    }
}
