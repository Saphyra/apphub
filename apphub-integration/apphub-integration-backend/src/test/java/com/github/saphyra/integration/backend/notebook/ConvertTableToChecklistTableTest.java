package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_INVALID_TYPE;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class ConvertTableToChecklistTableTest extends TestBase {
    private static final String TITLE = "title";
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getConvertTableToChecklistTableResponse(language, accessTokenId, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void invalidType(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID listItemId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        Response response = NotebookActions.getConvertTableToChecklistTableResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_TYPE));
    }

    @Test
    public void convertTableToChecklistTable(){
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parent = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();

        UUID listItemId = NotebookActions.createTable(language, accessTokenId, request);

        Response conversionResponse = NotebookActions.getConvertTableToChecklistTableResponse(language, accessTokenId, listItemId);

        assertThat(conversionResponse.getStatusCode()).isEqualTo(200);

        NotebookActions.getUpdateChecklistTableRowStatusResponse(language, accessTokenId, listItemId, 0, true);

        ChecklistTableResponse checklistTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        assertThat(checklistTableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(checklistTableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(checklistTableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(checklistTableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);
        assertThat(checklistTableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(checklistTableResponse.getRowStatus().get(0)).isTrue();
    }
}
