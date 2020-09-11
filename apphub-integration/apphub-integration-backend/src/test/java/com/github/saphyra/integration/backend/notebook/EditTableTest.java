package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.KeyValuePair;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.ERROR_CODE_LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class EditTableTest extends TestBase {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String ORIGINAL_COLUMN_NAME = "original-column-name";
    private static final String ORIGINAL_COLUMN_VALUE = "original-column-value";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankColumnName(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    " "
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columnName")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void differentColumnAmount(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                ),
                new KeyValuePair<>(null, NEW_COLUMN_VALUE)
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

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

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    null
                )
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columnValue")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void tableHeadNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    UUID.randomUUID(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void tableJoinNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    UUID.randomUUID(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        Response response = NotebookActions.getEditTableResponse(language, accessTokenId, UUID.randomUUID(), editTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void columnDeleted() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Collections.emptyList())
            .columns(Collections.emptyList())
            .build();

        NotebookActions.editTable(language, accessTokenId, listItemId, editTableRequest);

        TableResponse tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getTableColumns()).isEmpty();
    }

    @Test
    public void columnNameAndColumnValueModified() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        NotebookActions.editTable(language, accessTokenId, listItemId, editTableRequest);

        TableResponse tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);

        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(NEW_COLUMN_VALUE);
    }

    @Test
    public void columnAdded() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest createTableRequest = CreateTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(ORIGINAL_COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createTableRequest);

        TableResponse originalTableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                ),
                new KeyValuePair<>(
                    null,
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                ),
                new KeyValuePair<>(
                    null,
                    NEW_COLUMN_VALUE
                )
            )))
            .build();

        NotebookActions.editTable(language, accessTokenId, listItemId, editTableRequest);

        TableResponse tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);

        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);

        TableHeadResponse tableHeadResponse = tableResponse.getTableHeads()
            .stream()
            .filter(tr -> !tr.getTableHeadId().equals(originalTableResponse.getTableHeads().get(0).getTableHeadId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("New TableHead was not created."));
        assertThat(tableHeadResponse.getColumnIndex()).isEqualTo(1);
        assertThat(tableHeadResponse.getContent()).isEqualTo(NEW_COLUMN_NAME);

        TableColumnResponse tableColumnResponse = tableResponse.getTableColumns()
            .stream()
            .filter(tcr -> !tcr.getTableJoinId().equals(originalTableResponse.getTableColumns().get(0).getTableJoinId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("New TableJoin was not created."));
        assertThat(tableColumnResponse.getContent()).isEqualTo(NEW_COLUMN_VALUE);
        assertThat(tableColumnResponse.getRowIndex()).isEqualTo(0);
        assertThat(tableColumnResponse.getColumnIndex()).isEqualTo(1);
    }
}
