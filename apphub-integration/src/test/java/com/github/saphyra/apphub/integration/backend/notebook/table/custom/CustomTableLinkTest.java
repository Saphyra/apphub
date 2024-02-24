package com.github.saphyra.apphub.integration.backend.notebook.table.custom;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
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
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        create_nullData(accessTokenId);
        create_nullLink(accessTokenId);
        create_blankLabel(accessTokenId);
        create(accessTokenId);

        UUID listItemId = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren()
            .get(0)
            .getId();
        TableResponse tableResponse = TableActions.getTable(accessTokenId, listItemId);

        verifyCreatedTable(tableResponse);

        edit_nullData(accessTokenId, listItemId, tableResponse);
        edit_nullLink(accessTokenId, listItemId, tableResponse);
        edit_blankLabel(accessTokenId, listItemId, tableResponse);
        edit(accessTokenId, listItemId, tableResponse);

        ListItemActions.deleteListItem(accessTokenId, listItemId);
    }

    private void verifyCreatedTable(TableResponse tableResponse) {
        Object data = tableResponse.getRows()
            .get(0)
            .getColumns()
            .get(0)
            .getData();
        Link link = OBJECT_MAPPER_WRAPPER.convertValue(data, Link.class);

        assertThat(link.getLabel()).isEqualTo(LABEL);
        assertThat(link.getUrl()).isEqualTo(URL);
    }

    private void edit_nullData(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.LINK,
            null
        );

        Response response = TableActions.getEditTableResponse(accessTokenId, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link", "must not be null");
    }

    private void edit_nullLink(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(NEW_LABEL)
            .url(null)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.LINK,
            link
        );

        Response response = TableActions.getEditTableResponse(accessTokenId, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link.url", "must not be null");
    }

    private void edit_blankLabel(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(" ")
            .url(NEW_URL)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.LINK,
            link
        );

        Response response = TableActions.getEditTableResponse(accessTokenId, listItemId, editTableRequest);

        ResponseValidator.verifyInvalidParam(response, "link.label", "must not be null or blank");
    }

    private void edit(UUID accessTokenId, UUID listItemId, TableResponse tableResponse) {
        Link link = Link.builder()
            .label(NEW_LABEL)
            .url(NEW_URL)
            .build();

        EditTableRequest editTableRequest = CustomTableUtils.createEditCustomTableRequest(
            NEW_TITLE,
            tableResponse.getTableHeads().get(0).getTableHeadId(),
            NEW_COLUMN_TITLE,
            tableResponse.getRows().get(0).getRowId(),
            tableResponse.getRows().get(0).getColumns().get(0).getColumnId(),
            ColumnType.LINK,
            link
        );

        TableActions.editTable(accessTokenId, listItemId, editTableRequest);

        tableResponse = TableActions.getTable(accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_TITLE);
        Object data = tableResponse.getRows()
            .get(0)
            .getColumns()
            .get(0)
            .getData();
        Link responseLink = OBJECT_MAPPER_WRAPPER.convertValue(data, Link.class);
        assertThat(responseLink.getLabel()).isEqualTo(NEW_LABEL);
        assertThat(responseLink.getUrl()).isEqualTo(NEW_URL);
    }

    private void create(UUID accessTokenId) {
        Link link = Link.builder()
            .label(LABEL)
            .url(URL)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        TableActions.createTable(accessTokenId, request);
    }

    private void create_blankLabel(UUID accessTokenId) {
        Link link = Link.builder()
            .label(" ")
            .url(URL)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "link.label", "must not be null or blank");
    }

    private void create_nullLink(UUID accessTokenId) {
        Link link = Link.builder()
            .label(LABEL)
            .url(null)
            .build();

        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, link);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "link.url", "must not be null");
    }

    private static void create_nullData(UUID accessTokenId) {
        CreateTableRequest request = CustomTableUtils.createCustomTableRequest(TITLE, COLUMN_TITLE, ColumnType.LINK, null);

        Response response = TableActions.getCreateTableResponse(accessTokenId, request);

        ResponseValidator.verifyInvalidParam(response, "link", "must not be null");
    }
}
