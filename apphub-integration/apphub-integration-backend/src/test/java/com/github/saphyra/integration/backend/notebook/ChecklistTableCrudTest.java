package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditChecklistTableRequest;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.KeyValuePair;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.CATEGORY_NOT_FOUND;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_TYPE;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class ChecklistTableCrudTest extends BackEndTest {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TITLE = "title";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test(dataProvider = "languageDataProvider")
    public void checklistCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - blank title
        CreateChecklistTableRequest create_blankTitleRequest = CreateChecklistTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(language, create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateChecklistTableRequest create_parentNotFoundRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyCategoryNotFound(language, create_parentNotFoundResponse);

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateChecklistTableRequest create_parentNotCategoryRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, create_parentNotCategoryRequest);
        verifyInvalidResponse(language, create_parentNotCategoryResponse);

        //Create - Blank column name
        CreateChecklistTableRequest create_blankColumnNameRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(" "))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_blankColumnNameResponse = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, create_blankColumnNameRequest);
        verifyInvalidParam(language, create_blankColumnNameResponse, "columnName", "must not be null or blank");

        //Create - Incorrect column amount
        CreateChecklistTableRequest create_incorrectColumnAmountRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE, COLUMN_VALUE)).build()))
            .build();
        Response create_incorrectColumnAmountResponse = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, create_incorrectColumnAmountRequest);
        verifyInvalidParam(language, create_incorrectColumnAmountResponse, "columns", "amount different");

        //Create - Null column value
        CreateChecklistTableRequest create_nullColumnValueRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList((String) null)).build()))
            .build();
        Response create_nullColumnValueResponse = NotebookActions.getCreateChecklistTableResponse(language, accessTokenId, create_nullColumnValueRequest);
        verifyInvalidParam(language, create_nullColumnValueResponse, "columnValue", "must not be null");

        //Create
        CreateChecklistTableRequest createRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, createRequest);

        //Get
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

        //Get - Not found
        Response get_notFoundResponse = NotebookActions.getChecklistTableResponse(language, accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(language, get_notFoundResponse);

        //Edit - Blank title
        EditChecklistTableRequest edit_blankTitleRequest = EditChecklistTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();
        Response edit_blankTitleResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(language, edit_blankTitleResponse, "title", "must not be null or blank");

        //Edit - Blank column name
        EditChecklistTableRequest edit_blankColumnNameRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    " "
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();
        Response edit_blankColumnValueResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(language, edit_blankColumnValueResponse, "columnName", "must not be null or blank");

        //Edit - Different column amount
        EditChecklistTableRequest edit_differentColumnAmountRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
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
        Response edit_differentColumnAmountResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(language, edit_differentColumnAmountResponse, "columns", "amount different");

        //Edit - Null column value
        EditChecklistTableRequest edit_nullColumnValueRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
                            null
                        )
                    ))
                    .build()
            )))
            .build();
        Response edit_nullColumnValueResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(language, edit_nullColumnValueResponse, "columnValue", "must not be null");

        //Edit - TableHead not found
        EditChecklistTableRequest edit_tableHeadNotFoundRequest = EditChecklistTableRequest.builder()
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
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();
        Response edit_tableHeadNotFoundResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, edit_tableHeadNotFoundRequest);
        verifyListItemNotFound(language, edit_tableHeadNotFoundResponse);

        //Edit - TableJoin not found
        EditChecklistTableRequest edit_tableJoinNotFoundRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
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
        Response edit_tableJoinNotFoundResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, listItemId, edit_tableJoinNotFoundRequest);
        verifyListItemNotFound(language, edit_tableJoinNotFoundResponse);

        //Edit - ListItem not found
        EditChecklistTableRequest edit_listItemNotFoundRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();
        Response edit_listItemNotFoundResponse = NotebookActions.getEditChecklistTableResponse(language, accessTokenId, UUID.randomUUID(), edit_listItemNotFoundRequest);
        verifyListItemNotFound(language, edit_listItemNotFoundResponse);

        //Edit - Column deleted
        EditChecklistTableRequest edit_columnDeletedRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();
        NotebookActions.editChecklistTable(language, accessTokenId, listItemId, edit_columnDeletedRequest);
        tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getTableColumns()).isEmpty();
        assertThat(tableResponse.getRowStatus()).isEmpty();

        //Edit - Column added
        EditChecklistTableRequest edit_columnAddedRequest = EditChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    null,
                    COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(false)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            null,
                            COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();
        NotebookActions.editChecklistTable(language, accessTokenId, listItemId, edit_columnAddedRequest);
        tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);
        assertThat(tableResponse.getRowStatus().get(0)).isFalse();

        //Check - ListItem not found
        Response check_listItemNotFoundResponse = NotebookActions.getUpdateChecklistTableRowStatusResponse(language, accessTokenId, listItemId, 1, false);
        verifyListItemNotFound(language, check_listItemNotFoundResponse);

        //Check
        NotebookActions.updateChecklistTableRowStatus(language, accessTokenId, listItemId, 0, true);
        tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getRowStatus().get(0)).isTrue();

        //Uncheck
        NotebookActions.updateChecklistTableRowStatus(language, accessTokenId, listItemId, 0, false);
        tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getRowStatus().get(0)).isFalse();

        //Edit - Modify values
        EditChecklistTableRequest edit_modifyValuesRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .rows((Arrays.asList(
                ChecklistTableRowRequest.<KeyValuePair<String>>builder()
                    .checked(true)
                    .columns(Arrays.asList(
                        new KeyValuePair<>(
                            tableResponse.getTableColumns().get(0).getTableJoinId(),
                            NEW_COLUMN_VALUE
                        )
                    ))
                    .build()
            )))
            .build();
        NotebookActions.editChecklistTable(language, accessTokenId, listItemId, edit_modifyValuesRequest);
        tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(NEW_COLUMN_VALUE);
        assertThat(tableResponse.getRowStatus().get(0)).isTrue();

        //Edit - Delete checked
        Response edit_deleteCheckedResponse = NotebookActions.getDeleteCheckedChecklistTableItemsResponse(language, accessTokenId, listItemId);
        assertThat(edit_deleteCheckedResponse.getStatusCode()).isEqualTo(200);
        tableResponse = NotebookActions.getChecklistTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTableColumns()).isEmpty();
        assertThat(tableResponse.getTableColumns()).isEmpty();

        //Delete
        NotebookActions.deleteListItem(language, accessTokenId, listItemId);
        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    private void verifyListItemNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    private void verifyInvalidResponse(Language language, Response create_parentNotCategoryResponse) {
        assertThat(create_parentNotCategoryResponse.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = create_parentNotCategoryResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    private void verifyCategoryNotFound(Language language, Response create_parentNotFoundResponse) {
        assertThat(create_parentNotFoundResponse.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = create_parentNotFoundResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, CATEGORY_NOT_FOUND));
    }

    private void verifyInvalidParam(Language language, Response response, String field, String value) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }
}
