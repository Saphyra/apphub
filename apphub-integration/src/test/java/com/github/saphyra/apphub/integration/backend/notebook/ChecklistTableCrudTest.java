package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.KeyValuePair;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistTableResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditChecklistTableRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyListItemNotFound;
import static org.assertj.core.api.Assertions.assertThat;

@Ignore //TODO restore after Notebook API update
public class ChecklistTableCrudTest extends BackEndTest {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TITLE = "title";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test
    public void checklistCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        //Create - blank title
        CreateChecklistTableRequest create_blankTitleRequest = CreateChecklistTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateChecklistTableResponse(accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateChecklistTableRequest create_parentNotFoundRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateChecklistTableResponse(accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateChecklistTableRequest create_parentNotCategoryRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateChecklistTableResponse(accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //Create - Blank column name
        CreateChecklistTableRequest create_blankColumnNameRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(" "))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();
        Response create_blankColumnNameResponse = NotebookActions.getCreateChecklistTableResponse(accessTokenId, create_blankColumnNameRequest);
        verifyInvalidParam(create_blankColumnNameResponse, "columnName", "must not be null or blank");

        //Create - Incorrect column amount
        CreateChecklistTableRequest create_incorrectColumnAmountRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE, COLUMN_VALUE)).build()))
            .build();
        Response create_incorrectColumnAmountResponse = NotebookActions.getCreateChecklistTableResponse(accessTokenId, create_incorrectColumnAmountRequest);
        verifyInvalidParam(create_incorrectColumnAmountResponse, "columns", "amount different");

        //Create - Null column value
        CreateChecklistTableRequest create_nullColumnValueRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList((String) null)).build()))
            .build();
        Response create_nullColumnValueResponse = NotebookActions.getCreateChecklistTableResponse(accessTokenId, create_nullColumnValueRequest);
        verifyInvalidParam(create_nullColumnValueResponse, "columnValue", "must not be null");

        //Create
        CreateChecklistTableRequest createRequest = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(COLUMN_VALUE)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(accessTokenId, createRequest);

        //Get
        ChecklistTableResponse tableResponse = NotebookActions.getChecklistTable(accessTokenId, listItemId);
        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);
        assertThat(tableResponse.getRowStatus().get(0).getChecked()).isTrue();

        //Get - Not found
        Response get_notFoundResponse = NotebookActions.getChecklistTableResponse(accessTokenId, UUID.randomUUID());
        verifyListItemNotFound(get_notFoundResponse);

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
        Response edit_blankTitleResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");

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
        Response edit_blankColumnValueResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(edit_blankColumnValueResponse, "columnName", "must not be null or blank");

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
        Response edit_differentColumnAmountResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(edit_differentColumnAmountResponse, "columns", "amount different");

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
        Response edit_nullColumnValueResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(edit_nullColumnValueResponse, "columnValue", "must not be null");

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
        Response edit_tableHeadNotFoundResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, listItemId, edit_tableHeadNotFoundRequest);
        verifyListItemNotFound(edit_tableHeadNotFoundResponse);

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
        Response edit_tableJoinNotFoundResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, listItemId, edit_tableJoinNotFoundRequest);
        verifyListItemNotFound(edit_tableJoinNotFoundResponse);

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
        Response edit_listItemNotFoundResponse = NotebookActions.getEditChecklistTableResponse(accessTokenId, UUID.randomUUID(), edit_listItemNotFoundRequest);
        verifyListItemNotFound(edit_listItemNotFoundResponse);

        //Edit - Column deleted
        EditChecklistTableRequest edit_columnDeletedRequest = EditChecklistTableRequest.builder()
            .title(NEW_TITLE)
            .columnNames(Collections.emptyList())
            .rows(Collections.emptyList())
            .build();
        tableResponse = NotebookActions.editChecklistTable(accessTokenId, listItemId, edit_columnDeletedRequest);
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
        tableResponse = NotebookActions.editChecklistTable(accessTokenId, listItemId, edit_columnAddedRequest);
        assertThat(tableResponse.getTitle()).isEqualTo(TITLE);
        assertThat(tableResponse.getTableHeads()).hasSize(1);
        assertThat(tableResponse.getTableHeads().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(COLUMN_NAME);
        assertThat(tableResponse.getTableColumns()).hasSize(1);
        assertThat(tableResponse.getTableColumns().get(0).getColumnIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getRowIndex()).isEqualTo(0);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(COLUMN_VALUE);
        assertThat(tableResponse.getRowStatus().get(0).getChecked()).isFalse();

        //Check - ListItem not found
        Response check_listItemNotFoundResponse = NotebookActions.getUpdateChecklistTableRowStatusResponse(accessTokenId, null, false);
        verifyListItemNotFound(check_listItemNotFoundResponse);

        //Check
        NotebookActions.updateChecklistTableRowStatus(accessTokenId, null, true);
        tableResponse = NotebookActions.getChecklistTable(accessTokenId, listItemId);
        assertThat(tableResponse.getRowStatus().get(0).getChecked()).isTrue();

        //Uncheck
        NotebookActions.updateChecklistTableRowStatus(accessTokenId, null, false);
        tableResponse = NotebookActions.getChecklistTable(accessTokenId, listItemId);
        assertThat(tableResponse.getRowStatus().get(0).getChecked()).isFalse();

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
        tableResponse = NotebookActions.editChecklistTable(accessTokenId, listItemId, edit_modifyValuesRequest);
        assertThat(tableResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(tableResponse.getTableHeads().get(0).getContent()).isEqualTo(NEW_COLUMN_NAME);
        assertThat(tableResponse.getTableColumns().get(0).getContent()).isEqualTo(NEW_COLUMN_VALUE);
        assertThat(tableResponse.getRowStatus().get(0).getChecked()).isTrue();

        //Edit - Delete checked
        Response edit_deleteCheckedResponse = NotebookActions.getDeleteCheckedChecklistTableItemsResponse(accessTokenId, listItemId);
        assertThat(edit_deleteCheckedResponse.getStatusCode()).isEqualTo(200);
        tableResponse = edit_deleteCheckedResponse.getBody().as(ChecklistTableResponse.class);
        assertThat(tableResponse.getTableColumns()).isEmpty();
        assertThat(tableResponse.getTableColumns()).isEmpty();

        //Delete
        NotebookActions.deleteListItem(accessTokenId, listItemId);
        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }
}
