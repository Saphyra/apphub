package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyListItemNotFound;
import static org.assertj.core.api.Assertions.assertThat;

public class ConvertTableToChecklistTableTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

    @Test(groups = {"be", "notebook"})
    public void convertTableToChecklistTable() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        UUID parent = NotebookActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        itemNotFound(accessTokenId);
        invalidType(accessTokenId);
        convert(accessTokenId, parent);
    }

    private static void itemNotFound(UUID accessTokenId) {
        Response itemNotFoundResponse = NotebookActions.getConvertTableToChecklistTableResponse(accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(itemNotFoundResponse);
    }

    private static void invalidType(UUID accessTokenId) {
        UUID listItemId = NotebookActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());
        Response invalidTypeResponse = NotebookActions.getConvertTableToChecklistTableResponse(accessTokenId, listItemId);
        verifyErrorResponse(invalidTypeResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void convert(UUID accessTokenId, UUID parent) {
        UUID listItemId;
        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();

        listItemId = NotebookActions.createTable(accessTokenId, request);

        Response conversionResponse = NotebookActions.getConvertTableToChecklistTableResponse(accessTokenId, listItemId);

        assertThat(conversionResponse.getStatusCode()).isEqualTo(200);

        UUID rowId = NotebookActions.getChecklistTable(accessTokenId, listItemId)
            .getRowStatus()
            .get(0)
            .getRowId();

        Response response = NotebookActions.getUpdateChecklistTableRowStatusResponse(accessTokenId, rowId, true);

        assertThat(response.getStatusCode()).isEqualTo(200);

        ChecklistTableResponse checklistTableResponse = NotebookActions.getChecklistTable(accessTokenId, listItemId);

        assertThat(checklistTableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(checklistTableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(checklistTableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);
        assertThat(checklistTableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(checklistTableResponse.getRowStatus().get(0).getChecked()).isTrue();
    }
}
