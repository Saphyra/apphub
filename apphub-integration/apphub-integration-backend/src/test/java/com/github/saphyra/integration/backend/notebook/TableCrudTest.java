package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditTableRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.TableResponse;
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

public class TableCrudTest extends BackEndTest {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TITLE = "title";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test(dataProvider = "languageDataProvider")
    public void tableCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - Blank title
        CreateTableRequest create_blankTitleRequest = CreateTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(language, create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateTableRequest create_parentNotFoundRequest = CreateTableRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyCategoryNotFound(language, create_parentNotFoundResponse);

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateTableRequest create_parentNotCategoryRequest = CreateTableRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_parentNotCategoryRequest);
        verifyInvalidType(language, create_parentNotCategoryResponse);

        //Create - Blank column name
        CreateTableRequest create_blankColumnNameRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(" "))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_blankColumnNameResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_blankColumnNameRequest);
        verifyInvalidParam(language, create_blankColumnNameResponse, "columnName", "must not be null or blank");

        //Create - Incorrect column amount
        CreateTableRequest create_incorrectColumnAmountRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE, COLUMN_VALUE)))
            .build();
        Response create_incorrectColumnAmountResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_incorrectColumnAmountRequest);
        verifyInvalidParam(language, create_incorrectColumnAmountResponse, "columns", "amount different");

        //Create - Null column value
        CreateTableRequest create_nullColumnValueRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList((String) null)))
            .build();
        Response create_nullColumnValueResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_nullColumnValueRequest);
        verifyInvalidParam(language, create_nullColumnValueResponse, "columnValue", "must not be null");

        //Create
        CreateTableRequest createRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        UUID listItemId = NotebookActions.createTable(language, accessTokenId, createRequest);
        TableResponse tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);

        //Get - ListItem not found
        Response get_listItemNotFoundResponse = NotebookActions.getTableResponse(language, accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(language, get_listItemNotFoundResponse);

        //Edit - Blank title
        EditTableRequest edit_blankTitleRequest = EditTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();
        Response edit_blankTitleResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(language, edit_blankTitleResponse, "title", "must not be null or blank");

        //Edit - Blank column name
        EditTableRequest edit_blankColumnNameRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    " "
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();
        Response edit_blankColumnNameResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(language, edit_blankColumnNameResponse, "columnName", "must not be null or blank");

        //Edit - Different column amount
        EditTableRequest edit_differentColumnAmountRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                ),
                new KeyValuePair<>(null, NEW_COLUMN_VALUE)
            )))
            .build();
        Response edit_differentColumnAmountResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(language, edit_differentColumnAmountResponse, "columns", "amount different");

        //Edit - Null column value
        EditTableRequest edit_nullColumnValueRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    null
                )
            )))
            .build();
        Response edit_nullColumnValueResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(language, edit_nullColumnValueResponse, "columnValue", "must not be null");

        //Edit - ColumnHead not found
        EditTableRequest edit_columnHeadNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    UUID.randomUUID(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();
        Response edit_columnHeadNotFoundResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_columnHeadNotFoundRequest);
        verifyListItemNotFound(language, edit_columnHeadNotFoundResponse);

        //Edit - TableJoin not found
        EditTableRequest edit_tableJoinNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
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
        Response edit_tableJoinNotFoundResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_tableJoinNotFoundRequest);
        verifyListItemNotFound(language, edit_tableJoinNotFoundResponse);

        //Edit - ListItem not found
        EditTableRequest edit_listItemNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();
        Response edit_listItemNotFoundResponse = NotebookActions.getEditTableResponse(language, accessTokenId, UUID.randomUUID(), edit_listItemNotFoundRequest);
        verifyListItemNotFound(language, edit_listItemNotFoundResponse);

        //Edit - Column deleted
        EditTableRequest edit_columnDeletedRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Collections.emptyList())
            .columns(Collections.emptyList())
            .build();
        NotebookActions.editTable(language, accessTokenId, listItemId, edit_columnDeletedRequest);
        tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads()).isEmpty();
        assertThat(tableResponse.getTableColumns()).isEmpty();

        //Edit - Column added
        EditTableRequest edit_columnAddedRequest = EditTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    null,
                    COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    null,
                    COLUMN_VALUE
                )
            )))
            .build();
        NotebookActions.editTable(language, accessTokenId, listItemId, edit_columnAddedRequest);
        tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);

        //Edit - Column modified
        EditTableRequest editTableRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableHeads().get(0).getTableHeadId(),
                    NEW_COLUMN_NAME
                )
            ))
            .columns(Arrays.asList(Arrays.asList(
                new KeyValuePair<>(
                    tableResponse.getTableColumns().get(0).getTableJoinId(),
                    NEW_COLUMN_VALUE
                )
            )))
            .build();
        NotebookActions.editTable(language, accessTokenId, listItemId, editTableRequest);
        tableResponse = NotebookActions.getTable(language, accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(NEW_COLUMN_VALUE);


        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    private void verifyListItemNotFound(Language language, Response edit_columnHeadNotFoundResponse) {
        assertThat(edit_columnHeadNotFoundResponse.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = edit_columnHeadNotFoundResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    private void verifyInvalidType(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(422);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    private void verifyCategoryNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
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
