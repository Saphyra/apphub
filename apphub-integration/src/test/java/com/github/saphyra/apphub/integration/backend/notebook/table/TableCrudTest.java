package com.github.saphyra.apphub.integration.backend.notebook.table;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.BiWrapper;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessTokenId);
        create_nullListItemType(accessTokenId);
        create_parentNotFound(accessTokenId);
        create_parentNotCategory(accessTokenId);
        create_blankColumnName(accessTokenId);
        create_nullRows(accessTokenId);
        create_nullRowIndex(accessTokenId);
        create_nullColumns(accessTokenId);
        create_incorrectColumnAmount(accessTokenId);
        create_nullColumnValue(accessTokenId);
        create_nullColumnType(accessTokenId);
        create_nullColumnIndex(accessTokenId);

        //Create
        BiWrapper<TableResponse, UUID> tableCreationResult = create(accessTokenId);
        TableResponse tableResponse = tableCreationResult.getEntity1();
        UUID listItemId = tableCreationResult.getEntity2();

        get_listItemNotFound(accessTokenId);

        edit_blankTitle(accessTokenId, listItemId, tableResponse);
        edit_blankColumnName(accessTokenId, listItemId, tableResponse);
        edit_differentColumnAmount(accessTokenId, listItemId, tableResponse);
        edit_nullColumnValue(accessTokenId, listItemId, tableResponse);
        edit_tableHeadNotFound(accessTokenId, listItemId, tableResponse);
        edit_columnNotFound(accessTokenId, listItemId, tableResponse);
        edit_listItemNotFound(accessTokenId, tableResponse);
        edit_columnDeleted(accessTokenId, listItemId);
        tableResponse = edit_columnAdded(accessTokenId, listItemId);
        edit_columnModified(accessTokenId, listItemId, tableResponse);
    }

    private static void create_blankTitle(UUID accessTokenId) {
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
        Response create_blankTitleResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_nullListItemType(UUID accessTokenId) {
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
        Response create_nullListItemTypeResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_nullListItemTypeRequest);
        verifyInvalidParam(create_nullListItemTypeResponse, "listItemType", "must not be null");
    }

    private static void create_parentNotFound(UUID accessTokenId) {
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
        Response create_parentNotFoundResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_parentNotCategory(UUID accessTokenId) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessTokenId, CreateTextRequest.builder().title("title").content("").build());
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
        Response create_parentNotCategoryResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create_blankColumnName(UUID accessTokenId) {
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
        Response create_blankColumnNameResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_blankColumnNameRequest);
        verifyInvalidParam(create_blankColumnNameResponse, "tableHead.content", "must not be null or blank");
    }

    private static void create_nullRows(UUID accessTokenId) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(null)
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);
        verifyInvalidParam(response, "rows", "must not be null");
    }

    private static void create_nullRowIndex(UUID accessTokenId) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(null)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);
        verifyInvalidParam(response, "row.rowIndex", "must not be null");
    }

    private static void create_nullColumns(UUID accessTokenId) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(null)
                .build()
            ))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);
        verifyInvalidParam(response, "row.columns", "must not be null");
    }

    private static void create_incorrectColumnAmount(UUID accessTokenId) {
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
        Response create_incorrectColumnAmountResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_incorrectColumnAmountRequest);
        verifyInvalidParam(create_incorrectColumnAmountResponse, "row.columns", "item count mismatch");
    }

    private static void create_nullColumnValue(UUID accessTokenId) {
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
        Response create_nullColumnValueResponse = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, create_nullColumnValueRequest);
        verifyInvalidParam(create_nullColumnValueResponse, "text", "must not be null");
    }

    private static void create_nullColumnType(UUID accessTokenId) {
        CreateTableRequest request = CreateTableRequest.builder()
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
                    .columnType(null)
                    .data("a")
                    .build()))
                .build()))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);
        verifyInvalidParam(response, "row.column.columnType", "must not be null");
    }

    private static void create_nullColumnIndex(UUID accessTokenId) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(null)
                    .columnType(ColumnType.TEXT)
                    .data("")
                    .build()))
                .build()))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessTokenId, request);
        verifyInvalidParam(response, "row.column.columnIndex", "must not be null");
    }

    private static BiWrapper<TableResponse, UUID> create(UUID accessTokenId) {
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
        TableActions.createTable(getServerPort(), accessTokenId, createRequest);
        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, null)
            .getChildren()
            .stream()
            .filter(notebookView -> notebookView.getTitle().equals(TABLE_TITLE))
            .map(NotebookView::getId)
            .findAny()
            .orElseThrow(() -> new RuntimeException("Table was not created."));
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(COLUMN_VALUE);

        return new BiWrapper<>(tableResponse, listItemId);
    }

    private static void get_listItemNotFound(UUID accessTokenId) {
        Response get_listItemNotFoundResponse = TableActions.getTableResponse(getServerPort(), accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(get_listItemNotFoundResponse);
    }

    private static void edit_blankTitle(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        Response edit_blankTitleResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void edit_blankColumnName(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        Response edit_blankColumnNameResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(edit_blankColumnNameResponse, "tableHead.content", "must not be null or blank");
    }

    private static void edit_differentColumnAmount(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        Response edit_differentColumnAmountResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(edit_differentColumnAmountResponse, "row.columns", "item count mismatch");
    }

    private static void edit_nullColumnValue(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        Response edit_nullColumnValueResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(edit_nullColumnValueResponse, "text", "must not be null");
    }

    private static void edit_tableHeadNotFound(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        Response edit_columnHeadNotFoundResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, listItemId, edit_columnHeadNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_columnHeadNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_columnNotFound(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        Response edit_tableJoinNotFoundResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, listItemId, edit_tableJoinNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_tableJoinNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_listItemNotFound(UUID accessTokenId, TableResponse tableResponse) {
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
        Response edit_listItemNotFoundResponse = TableActions.getEditTableResponse(getServerPort(), accessTokenId, UUID.randomUUID(), edit_listItemNotFoundRequest);
        ResponseValidator.verifyInvalidParam(edit_listItemNotFoundResponse, "tableHead.tableHeadId", "points to different table");
    }

    private static void edit_columnDeleted(UUID accessTokenId, UUID listItemId) {
        TableResponse tableResponse;
        EditTableRequest edit_columnDeletedRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();
        TableActions.editTable(getServerPort(), accessTokenId, listItemId, edit_columnDeletedRequest);
        tableResponse = TableActions.getTable(getServerPort(), accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getRows()).isEmpty();
    }

    private static TableResponse edit_columnAdded(UUID accessTokenId, UUID listItemId) {
        TableResponse tableResponse;
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
        TableActions.editTable(getServerPort(), accessTokenId, listItemId, edit_columnAddedRequest);
        tableResponse = TableActions.getTable(getServerPort(), accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(COLUMN_VALUE);
        return tableResponse;
    }

    private static void edit_columnModified(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
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
        TableActions.editTable(getServerPort(), accessTokenId, listItemId, editTableRequest);
        tableResponse = TableActions.getTable(getServerPort(), accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getRows().get(0).getColumns().get(0).getData()).isEqualTo(NEW_COLUMN_VALUE);

        ListItemActions.deleteListItem(getServerPort(), accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = CategoryActions.getCategoryTree(getServerPort(), accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }
}
