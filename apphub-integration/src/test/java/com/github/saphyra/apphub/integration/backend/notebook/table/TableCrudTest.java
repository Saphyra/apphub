package com.github.saphyra.apphub.integration.backend.notebook.table;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessToken);
        create_tooLongTitle(accessToken);
        create_nullListItemType(accessToken);
        create_parentNotFound(accessToken);
        create_parentNotCategory(accessToken);
        create_blankColumnName(accessToken);
        create_tooLongColumnName(accessToken);
        create_tooLongColumnValue(accessToken);
        create_nullRows(accessToken);
        create_nullRowIndex(accessToken);
        create_nullColumns(accessToken);
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
        edit_tooLongTitle(accessToken, listItemId, tableResponse);
        edit_blankColumnName(accessToken, listItemId, tableResponse);
        edit_tooLongColumnName(accessToken, listItemId, tableResponse);
        edit_tooLongColumnValue(accessToken, listItemId, tableResponse);
        edit_differentColumnAmount(accessToken, listItemId, tableResponse);
        edit_nullColumnValue(accessToken, listItemId, tableResponse);
        edit_tableHeadNotFound(accessToken, listItemId, tableResponse);
        edit_columnNotFound(accessToken, listItemId, tableResponse);
        edit_listItemNotFound(accessToken, tableResponse);
        edit_columnDeleted(accessToken, listItemId);
        tableResponse = edit_columnAdded(accessToken, listItemId);
        edit_columnModified(accessToken, listItemId, tableResponse);
    }

    private static CreateTableRequest validCreateRequest() {
        return CreateTableRequest.builder()
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
    }

    private static EditTableRequest validEditRequest(TableResponse tableResponse) {
        return EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                .columnIndex(0)
                .content(NEW_COLUMN_NAME)
                .type(ItemType.EXISTING)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()))
            .build();
    }

    private static void create_blankTitle(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .title(" ")
            .build());
        verifyInvalidParam(response, "title", "must not be null or blank");
    }

    private static void create_tooLongTitle(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .title("a".repeat(Constants.MAX_LIST_ITEM_TITLE_LENGTH + 1))
            .build());
        verifyInvalidParam(response, "title", "too long");
    }

    private static void create_nullListItemType(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .listItemType(null)
            .build());
        verifyInvalidParam(response, "listItemType", "must not be null");
    }

    private static void create_parentNotFound(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .parent(UUID.randomUUID())
            .build());
        verifyErrorResponse(response, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_parentNotCategory(String accessToken) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title("title").content("").build());
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .parent(notCategoryParentId)
            .build());
        verifyErrorResponse(response, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create_blankColumnName(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(" ")
                .build()))
            .build());
        verifyInvalidParam(response, "tableHead.content", "must not be null or blank");
    }

    private static void create_tooLongColumnName(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content("a".repeat(Constants.MAX_LIST_ITEM_TITLE_LENGTH + 1))
                .build()))
            .build());
        verifyInvalidParam(response, "tableHead.content", "too long");
    }

    private static void create_tooLongColumnValue(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data("a".repeat(Constants.MAX_LIST_ITEM_CONTENT_LENGTH + 1))
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "data", "too long");
    }

    private static void create_nullRows(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(null)
            .build());
        verifyInvalidParam(response, "rows", "must not be null");
    }

    private static void create_nullRowIndex(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowIndex(null)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "row.rowIndex", "must not be null");
    }

    private static void create_nullColumns(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(null)
                .build()))
            .build());
        verifyInvalidParam(response, "row.columns", "must not be null");
    }

    private static void create_incorrectColumnAmount(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
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
            .build());
        verifyInvalidParam(response, "row.columns", "item count mismatch");
    }

    private static void create_nullColumnValue(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .data(null)
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "data", "must not be null");
    }

    private static void create_nullColumnType(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(null)
                    .data("a")
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "row.column.columnType", "must not be null");
    }

    private static void create_nullColumnIndex(String accessToken) {
        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, validCreateRequest().toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(null)
                    .columnType(ColumnType.TEXT)
                    .data("")
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "row.column.columnIndex", "must not be null");
    }

    private static BiWrapper<TableResponse, UUID> create(String accessToken) {
        TableActions.createTable(getServerPort(), accessToken, validCreateRequest());
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
        assertThat(tableResponse.getRows().getFirst().getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(COLUMN_VALUE);

        return new BiWrapper<>(tableResponse, listItemId);
    }

    private static void get_listItemNotFound(String accessToken) {
        Response response = TableActions.getTableResponse(getServerPort(), accessToken, UUID.randomUUID());
        verifyListItemNotFound(response);
    }

    private static void edit_blankTitle(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .title(" ")
            .build());
        verifyInvalidParam(response, "title", "must not be null or blank");
    }

    private static void edit_tooLongTitle(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .title("a".repeat(Constants.MAX_LIST_ITEM_TITLE_LENGTH + 1))
            .build());
        verifyInvalidParam(response, "title", "too long");
    }

    private static void edit_blankColumnName(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .tableHeads(List.of(TableHeadModel.builder()
                .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                .columnIndex(0)
                .content(" ")
                .type(ItemType.EXISTING)
                .build()))
            .build());
        verifyInvalidParam(response, "tableHead.content", "must not be null or blank");
    }

    private static void edit_tooLongColumnName(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .tableHeads(List.of(TableHeadModel.builder()
                .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                .columnIndex(0)
                .content("a".repeat(Constants.MAX_LIST_ITEM_TITLE_LENGTH + 1))
                .type(ItemType.EXISTING)
                .build()))
            .build());
        verifyInvalidParam(response, "tableHead.content", "too long");
    }

    private static void edit_tooLongColumnValue(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data("a".repeat(Constants.MAX_LIST_ITEM_CONTENT_LENGTH + 1))
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "data", "too long");
    }

    private static void edit_differentColumnAmount(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
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
                .build()))
            .build());
        verifyInvalidParam(response, "row.columns", "item count mismatch");
    }

    private static void edit_nullColumnValue(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(null)
                    .build()))
                .build()))
            .build());
        verifyInvalidParam(response, "data", "must not be null");
    }

    private static void edit_tableHeadNotFound(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .tableHeads(List.of(TableHeadModel.builder()
                .tableHeadId(UUID.randomUUID())
                .columnIndex(0)
                .content(NEW_COLUMN_NAME)
                .type(ItemType.EXISTING)
                .build()))
            .build());
        ResponseValidator.verifyErrorResponse(response, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_columnNotFound(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, validEditRequest(tableResponse).toBuilder()
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(UUID.randomUUID())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_NAME)
                    .build()))
                .build()))
            .build());
        ResponseValidator.verifyErrorResponse(response, 404, ErrorCode.DATA_NOT_FOUND);
    }

    private static void edit_listItemNotFound(String accessToken, TableResponse tableResponse) {
        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, UUID.randomUUID(), validEditRequest(tableResponse));
        ResponseValidator.verifyErrorResponse(response, 404, ErrorCode.LIST_ITEM_NOT_FOUND);
    }

    private static void edit_columnDeleted(String accessToken, UUID listItemId) {
        EditTableRequest request = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();
        TableActions.editTable(getServerPort(), accessToken, listItemId, request);
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getRows()).isEmpty();
    }

    private static TableResponse edit_columnAdded(String accessToken, UUID listItemId) {
        EditTableRequest request = EditTableRequest.builder()
            .title(TABLE_TITLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .columnIndex(0)
                .content(COLUMN_NAME)
                .type(ItemType.NEW)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowIndex(0)
                .itemType(ItemType.NEW)
                .columns(List.of(TableColumnModel.builder()
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.NEW)
                    .data(COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        TableActions.editTable(getServerPort(), accessToken, listItemId, request);
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TABLE_TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getRows()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getColumns()).hasSize(1);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(COLUMN_VALUE);
        return tableResponse;
    }

    private static void edit_columnModified(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest request = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(List.of(TableHeadModel.builder()
                .tableHeadId(tableResponse.getTableHeads().getFirst().getTableHeadId())
                .columnIndex(0)
                .content(NEW_COLUMN_NAME)
                .type(ItemType.EXISTING)
                .build()))
            .rows(List.of(TableRowModel.builder()
                .rowId(tableResponse.getRows().getFirst().getRowId())
                .rowIndex(0)
                .itemType(ItemType.EXISTING)
                .columns(List.of(TableColumnModel.builder()
                    .columnId(tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId())
                    .columnIndex(0)
                    .columnType(ColumnType.TEXT)
                    .itemType(ItemType.EXISTING)
                    .data(NEW_COLUMN_VALUE)
                    .build()))
                .build()))
            .build();
        TableActions.editTable(getServerPort(), accessToken, listItemId, request);
        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getRows().getFirst().getColumns().getFirst().getData()).isEqualTo(NEW_COLUMN_VALUE);

        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);

        List<CategoryTreeView> categoryTreeViews = CategoryActions.getCategoryTree(getServerPort(), accessToken);
        assertThat(categoryTreeViews).isEmpty();
    }
}
