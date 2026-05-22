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
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
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

public class ChecklistTableCrudTest extends BackEndTest {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TABLE_TITLE = "table-title";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test(groups = {"be", "notebook"})
    public void checklistTableCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessToken);
        create_nullListItemType(accessToken);
        create_parentNotFound(accessToken);
        create_parentNotCategory(accessToken);
        create_blankColumnName(accessToken);
        create_nullRows(accessToken);
        create_nullRowIndex(accessToken);
        create_nullColumns(accessToken);
        create_nullChecked(accessToken);
        create_incorrectColumnAmount(accessToken);
        create_nullColumnValue(accessToken);
        create_nullColumnType(accessToken);
        create_nullColumnIndex(accessToken);

        //Create
        BiWrapper<TableResponse, UUID> tableCreationResult = create(accessToken);
        TableResponse tableResponse = tableCreationResult.getEntity1();
        UUID listItemId = tableCreationResult.getEntity2();

        get_listItemNotFound(accessToken);

        edit_blankTitle(accessToken, listItemId, tableResponse);
        edit_blankColumnName(accessToken, listItemId, tableResponse);
        edit_differentColumnAmount(accessToken, listItemId, tableResponse);
        edit_nullChecked(accessToken, listItemId, tableResponse);
        edit_nullColumnValue(accessToken, listItemId, tableResponse);
        edit_tableHeadNotFound(accessToken, listItemId, tableResponse);
        edit_columnNotFound(accessToken, listItemId, tableResponse);
        edit_listItemNotFound(accessToken, tableResponse);
        edit_columnDeleted(accessToken, listItemId);
        tableResponse = edit_columnAdded(accessToken, listItemId);
        tableResponse = edit_columnModified(accessToken, listItemId, tableResponse);

        changeRowStatus_nullStatus(accessToken, listItemId, tableResponse);
        changeRowStatus(accessToken, listItemId, tableResponse);

        delete(accessToken, listItemId);
    }

    private void changeRowStatus(String accessToken, UUID listItemId, TableResponse tableResponse) {
        UUID rowId = tableResponse.getRows().getFirst().getRowId();
        TableActions.getUpdateChecklistTableRowStatusResponse(getServerPort(), accessToken, listItemId, rowId, false);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getRows().getFirst().getChecked()).isFalse();
    }

    private void changeRowStatus_nullStatus(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getUpdateChecklistTableRowStatusResponse(getServerPort(), accessToken, listItemId, tableResponse.getRows().getFirst().getRowId(), null);

        ResponseValidator.verifyInvalidParam(response, "status", "must not be null");
    }

    private static void create_blankTitle(String accessToken) {
        CreateTableRequest create_blankTitleRequest = CreateTableRequest.builder()
            .title(" ")
            .listItemType(ListItemType.CHECKLIST_TABLE)
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
        Response create_blankTitleResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_nullListItemType(String accessToken) {
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
        Response create_nullListItemTypeResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_nullListItemTypeRequest);
        verifyInvalidParam(create_nullListItemTypeResponse, "listItemType", "must not be null");
    }

    private static void create_parentNotFound(String accessToken) {
        CreateTableRequest create_parentNotFoundRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
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
        Response create_parentNotFoundResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_parentNotCategory(String accessToken) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title("title").content("").build());
        CreateTableRequest create_parentNotCategoryRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
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
        Response create_parentNotCategoryResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create_blankColumnName(String accessToken) {
        CreateTableRequest create_blankColumnNameRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
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
        Response create_blankColumnNameResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_blankColumnNameRequest);
        verifyInvalidParam(create_blankColumnNameResponse, "tableHead.content", "must not be null or blank");
    }

    private static void create_nullRows(String accessToken) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(null)
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(response, "rows", "must not be null");
    }

    private static void create_nullRowIndex(String accessToken) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
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
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(response, "row.rowIndex", "must not be null");
    }

    private static void create_nullColumns(String accessToken) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
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
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(response, "row.columns", "must not be null");
    }

    private static void create_nullChecked(String accessToken) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .checked(null)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(response, "row.checked", "must not be null");
    }

    private static void create_incorrectColumnAmount(String accessToken) {
        CreateTableRequest create_incorrectColumnAmountRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .checked(true)
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
        Response create_incorrectColumnAmountResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_incorrectColumnAmountRequest);
        verifyInvalidParam(create_incorrectColumnAmountResponse, "row.columns", "item count mismatch");
    }

    private static void create_nullColumnValue(String accessToken) {
        CreateTableRequest create_nullColumnValueRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .checked(true)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(null)
                    .build()))
                .build()))
            .build();
        Response create_nullColumnValueResponse = TableActions.getCreateTableResponse(getServerPort(), accessToken, create_nullColumnValueRequest);
        verifyInvalidParam(create_nullColumnValueResponse, "data", "must not be null");
    }

    private static void create_nullColumnType(String accessToken) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .checked(true)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(null)
                    .data("a")
                    .build()))
                .build()))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(response, "row.column.columnType", "must not be null");
    }

    private static void create_nullColumnIndex(String accessToken) {
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .checked(true)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(null)
                    .columnType(ColumnType.TEXT)
                    .data("")
                    .build()))
                .build()))
            .build();
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);
        verifyInvalidParam(response, "row.column.columnIndex", "must not be null");
    }

    private static BiWrapper<TableResponse, UUID> create(String accessToken) {
        CreateTableRequest createRequest = CreateTableRequest.builder()
            .title(TABLE_TITLE)
            .listItemType(ListItemType.CHECKLIST_TABLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .checked(true)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        TableActions.createTable(getServerPort(), accessToken, createRequest);
        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .stream()
            .filter(notebookView -> notebookView.getTitle().equals(TABLE_TITLE))
            .map(NotebookView::getId)
            .findAny()
            .orElseThrow(() -> new RuntimeException("Table was not created."));
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getChecked()).isTrue();
        assertThat(tableResponse.getRows().getFirst().getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(COLUMN_VALUE);

        return new BiWrapper<>(tableResponse, listItemId);
    }

    private static void get_listItemNotFound(String accessToken) {
        Response get_listItemNotFoundResponse = TableActions.getTableResponse(getServerPort(), accessToken, UUID.randomUUID());
        verifyListItemNotFound(get_listItemNotFoundResponse);
    }

    private static void edit_blankTitle(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest edit_blankTitleRequest = EditTableRequest.builder()
            .title(" ")
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_blankTitleResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void edit_blankColumnName(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest edit_blankColumnNameRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(" ")
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_blankColumnNameResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(edit_blankColumnNameResponse, "tableHead.content", "must not be null or blank");
    }

    private static void edit_differentColumnAmount(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest edit_differentColumnAmountRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(
                    TableColumnModel.builder()
                        .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
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
        Response edit_differentColumnAmountResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(edit_differentColumnAmountResponse, "row.columns", "item count mismatch");
    }

    private static void edit_nullChecked(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest edit_differentColumnAmountRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(null)
                .itemType(ItemType.EXISTING)
                .columns(List.of(
                    TableColumnModel.builder()
                        .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                        .columnIndex(0)
                        .columnType(ColumnType.TEXT)
                        .itemType(ItemType.EXISTING)
                        .data(NEW_COLUMN_NAME)
                        .build()
                ))
                .build()
            ))
            .build();
        Response edit_differentColumnAmountResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(edit_differentColumnAmountResponse, "row.checked", "must not be null");
    }

    private static void edit_nullColumnValue(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest edit_nullColumnValueRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(null)
                    .build()))
                .build()
            ))
            .build();
        Response edit_nullColumnValueResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(edit_nullColumnValueResponse, "data", "must not be null");
    }

    private static void edit_tableHeadNotFound(String accessToken, UUID listItemId, TableResponse tableResponse) {
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
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_columnHeadNotFoundResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_columnHeadNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_columnHeadNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_columnNotFound(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest edit_tableJoinNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
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
        Response edit_tableJoinNotFoundResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, edit_tableJoinNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_tableJoinNotFoundResponse, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_listItemNotFound(String accessToken, TableResponse tableResponse) {
        EditTableRequest edit_listItemNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()
            ))
            .build();
        Response edit_listItemNotFoundResponse = TableActions.getEditTableResponse(getServerPort(), accessToken, UUID.randomUUID(), edit_listItemNotFoundRequest);
        ResponseValidator.verifyErrorResponse(edit_listItemNotFoundResponse, 404, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    private static void edit_columnDeleted(String accessToken, UUID listItemId) {
        TableResponse tableResponse;
        EditTableRequest edit_columnDeletedRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();
        TableActions.editTable(getServerPort(), accessToken, listItemId, edit_columnDeletedRequest);
        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getRows()).isEmpty();
    }

    private static TableResponse edit_columnAdded(String accessToken, UUID listItemId) {
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
                .checked(true)
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
        TableActions.editTable(getServerPort(), accessToken, listItemId, edit_columnAddedRequest);
        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getChecked()).isTrue();
        assertThat(tableResponse.getRows().getFirst().getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(COLUMN_VALUE);
        return tableResponse;
    }

    private static TableResponse edit_columnModified(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(TableHeadModel.builder()
                    .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                    .columnIndex(0)
                    .content(NEW_COLUMN_NAME)
                    .type(ItemType.EXISTING)
                    .build()
                ))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .checked(true)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_VALUE)
                    .build()))
                .build()
            ))
            .build();
        tableResponse = TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest)
            .getTableResponse();
        assertThat(tableResponse.getRows().getFirst().getChecked()).isTrue();
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(NEW_COLUMN_VALUE);

        return tableResponse;
    }

    private static void delete(String accessToken, UUID listItemId) {
        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);

        List<CategoryTreeView> categoryTreeViews = CategoryActions.getCategoryTree(getServerPort(), accessToken);
        assertThat(categoryTreeViews).isEmpty();
    }
}
