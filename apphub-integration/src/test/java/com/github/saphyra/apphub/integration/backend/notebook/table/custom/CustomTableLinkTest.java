package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.Link;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class CustomTableLinkTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_TITLE = "column-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_COLUMN_TITLE = "new-column-title";
    private static final String LABEL = "label";
    private static final String URL = "url";
    private static final String NEW_LABEL = "new-label";
    private static final String NEW_URL = "new-url";

    @Test(groups = {"be", "notebook"})
    public void customTableLinkCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_nullData(accessToken);
        create_nullLink(accessToken);
        create_tooLongLink(accessToken);
        create_blankLabel(accessToken);
        create_tooLongLabel(accessToken);
        create(accessToken);

        UUID listItemId = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren()
            .getFirst()
            .getId();
        TableResponse tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        verifyCreatedTable(tableResponse);

        edit_nullData(accessToken, listItemId, tableResponse);
        edit_nullLink(accessToken, listItemId, tableResponse);
        edit_tooLongLink(accessToken, listItemId, tableResponse);
        edit_blankLabel(accessToken, listItemId, tableResponse);
        edit_tooLongLabel(accessToken, listItemId, tableResponse);
        edit(accessToken, listItemId, tableResponse);

        ListItemActions.deleteListItem(getServerPort(), accessToken, listItemId);
    }

    private void verifyCreatedTable(TableResponse tableResponse) {
        Object data = tableResponse.getRows()
            .getFirst()
            .getColumns()
            .getFirst()
            .getData();
        Link link = OBJECT_MAPPER_WRAPPER.convertValue(data, Link.class);

        assertThat(link.getLabel()).isEqualTo(LABEL);
        assertThat(link.getUrl()).isEqualTo(URL);
    }

    private void edit_nullData(String accessToken, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.LINK,
            null
        );

        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link", "must not be null");
    }

    private void edit_nullLink(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(NEW_LABEL)
            .url(null)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.LINK,
            link
        );

        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link.url", "must not be null");
    }

    private void edit_tooLongLink(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(NEW_LABEL)
            .url("a".repeat(Constants.MAX_LIST_ITEM_CONTENT_LENGTH + 1))
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.LINK,
            link
        );

        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link.url", "too long");
    }

    private void edit_blankLabel(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(" ")
            .url(NEW_URL)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.LINK,
            link
        );

        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link.label", "must not be null or blank");
    }

    private void edit_tooLongLabel(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label("a".repeat(Constants.MAX_LIST_ITEM_CONTENT_LENGTH + 1))
            .url(NEW_URL)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.LINK,
            link
        );

        Response response = TableActions.getEditTableResponse(getServerPort(), accessToken, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link.label", "too long");
    }

    private void edit(String accessToken, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(NEW_LABEL)
            .url(NEW_URL)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().getFirst().getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().getFirst().getRowId(),
            tableResponse.getRows().getFirst().getColumns().getFirst().getColumnId(),
            ColumnType.LINK,
            link
        );

        TableActions.editTable(getServerPort(), accessToken, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(getServerPort(), accessToken, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().getFirst().getContent()).isEqualTo(NEW_COLUMN_TITLE);
        Object data = tableResponse.getRows()
            .getFirst()
            .getColumns()
            .getFirst()
            .getData();
        Link responseLink = OBJECT_MAPPER_WRAPPER.convertValue(data, Link.class);
        assertThat(responseLink.getLabel()).isEqualTo(NEW_LABEL);
        assertThat(responseLink.getUrl()).isEqualTo(NEW_URL);
    }

    private void create(String accessToken) {
        Link link = Link.builder()
            .label(LABEL)
            .url(URL)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        TableActions.createTable(getServerPort(), accessToken, request);
    }

    private void create_blankLabel(String accessToken) {
        Link link = Link.builder()
            .label(" ")
            .url(URL)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "link.label", "must not be null or blank");
    }

    private void create_tooLongLabel(String accessToken) {
        Link link = Link.builder()
            .label("a".repeat(Constants.MAX_LIST_ITEM_CONTENT_LENGTH + 1))
            .url(URL)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "link.label", "too long");
    }

    private void create_nullLink(String accessToken) {
        Link link = Link.builder()
            .label(LABEL)
            .url(null)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "link.url", "must not be null");
    }

    private void create_tooLongLink(String accessToken) {
        Link link = Link.builder()
            .label(LABEL)
            .url("a".repeat(Constants.MAX_LIST_ITEM_CONTENT_LENGTH + 1))
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "link.url", "too long");
    }

    private static void create_nullData(String accessToken) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, null);

        Response response = TableActions.getCreateTableResponse(getServerPort(), accessToken, request);

        ResponseValidator.verifyInvalidParam(response, "link", "must not be null");
    }
}
