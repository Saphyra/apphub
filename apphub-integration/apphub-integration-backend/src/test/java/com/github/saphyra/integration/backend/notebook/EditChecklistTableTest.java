package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
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

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class EditChecklistTableTest extends BackEndTest {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String ORIGINAL_COLUMN_NAME = "original-column-name";
    private static final String ORIGINAL_COLUMN_VALUE = "original-column-value";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankColumnName(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    " "
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columnName")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void differentColumnAmount(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        ),
                        new KeyValuePair<>(
                            null,
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columns")).isEqualTo("amount different");
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullColumnValue(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            null
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("columnValue")).isEqualTo("must not be null");
    }

    @Test(dataProvider = "localeDataProvider")
    public void tableHeadNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    UUID.randomUUID(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void tableJoinNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            UUID.randomUUID(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        Response response = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, UUID.randomUUID(), editChecklistTableRequest);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void columnDeleted() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();

        NotebookActions.editChecklistTable(language, accessTokenId, listItemId, editChecklistTableRequest);

        ChecklistTableResponse checklistTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        assertThat(checklistTableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistTableResponse.getTableHeads()).isEmpty();
        assertThat(checklistTableResponse.getTableColumns()).isEmpty();
        assertThat(checklistTableResponse.getRowStatus()).isEmpty();
    }

    @Test
    public void columnNameAndColumnValueModified() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    originalTableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        NotebookActions.editChecklistTable(language, accessTokenId, listItemId, editChecklistTableRequest);

        ChecklistTableResponse checklistTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        assertThat(checklistTableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(checklistTableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(checklistTableResponse.getTableColumns().get(0).getContent()).isEqualTo(NEW_COLUMN_VALUE);
        assertThat(checklistTableResponse.getRowStatus().get(0)).isTrue();
    }

    @Test
    public void columnAdded() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest createTableRequest = CreateChecklistTableRequest.builder()
            .title(ORIGINAL_TITLE)
            .columnNames(Arrays.asList(ORIGINAL_COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(false).columns(Arrays.asList(ORIGINAL_COLUMN_VALUE)).build()))
            .build();
        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createTableRequest);

        ChecklistTableResponse originalTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        EditChecklistTableRequest editChecklistTableRequest = EditChecklistTableRequest.builder()
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
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            originalTableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        ),
                        new KeyValuePair<>(
                            null,
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();

        NotebookActions.editChecklistTable(language, accessTokenId, listItemId, editChecklistTableRequest);

        ChecklistTableResponse checklistTableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);

        assertThat(checklistTableResponse.getTitle()).isEqualTo(NEW_TITLE);

        TableHeadResponse tableHeadResponse = checklistTableResponse.getTableHeads()
            .stream()
            .filter(tr -> !tr.getTableHeadId().equals(originalTableResponse.getTableHeads().get(0).getTableHeadId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("New TableHead was not created."));
        assertThat(tableHeadResponse.getColumnIndex()).isEqualTo(1);
        assertThat(tableHeadResponse.getContent()).isEqualTo(NEW_COLUMN_NAME);

        TableColumnResponse tableColumnResponse = checklistTableResponse.getTableColumns()
            .stream()
            .filter(tcr -> !tcr.getTableJoinId().equals(originalTableResponse.getTableColumns().get(0).getTableJoinId()))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("New TableJoin was not created."));
        assertThat(tableColumnResponse.getContent()).isEqualTo(NEW_COLUMN_VALUE);
        assertThat(tableColumnResponse.getRowIndex()).isEqualTo(0);
        assertThat(tableColumnResponse.getColumnIndex()).isEqualTo(1);
        assertThat(checklistTableResponse.getRowStatus().get(0)).isTrue();
    }
}
