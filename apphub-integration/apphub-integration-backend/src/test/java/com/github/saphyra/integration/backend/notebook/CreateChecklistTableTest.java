package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
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

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.*;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_INVALID_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateChecklistTableTest extends TestBase {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TITLE = "title";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_CATEGORY_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotCategory(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parent = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(422);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_TYPE));
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankColumnName(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parent = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(" "))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        Response response = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columnName")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void incorrectColumnAmount(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parent = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE, COLUMN_VALUE)).build()))
            .build();

        Response response = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columns")).isEqualTo("amount different");
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullColumnValue(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parent = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList((String) null)).build()))
            .build();

        Response response = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columnValue")).isEqualTo("must not be null");
    }

    @Test
    public void successfullyCreated() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parent = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(TITLE).build());

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(parent)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, request);

        ChecklistTableResponse tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);

        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);

        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);

        assertThat(tableResponse.getRowStatus().get(0)).isTrue();
    }
}
