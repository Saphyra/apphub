package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTableHeadRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTableJoinRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.TableResponse;
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

@Ignore //TODO restore when new API was implemented
public class TableCrudTest extends BackEndTest {
    private static final String COLUMN_NAME = "column-name";
    private static final String COLUMN_VALUE = "column-value";
    private static final String TITLE = "title";
    private static final String NEW_COLUMN_NAME = "new-column-name";
    private static final String NEW_COLUMN_VALUE = "new-column-value";
    private static final String NEW_TITLE = "new-title";

    @Test
    public void tableCrud() {
        Language language = Language.ENGLISH;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - Blank title
        CreateTableRequest create_blankTitleRequest = CreateTableRequest.builder()
            .title(" ")
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateTableRequest create_parentNotFoundRequest = CreateTableRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateTableRequest create_parentNotCategoryRequest = CreateTableRequest.builder()
            .title(TITLE)
            .parent(notCategoryParentId)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //Create - Blank column name
        CreateTableRequest create_blankColumnNameRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(" "))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE)))
            .build();
        Response create_blankColumnNameResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_blankColumnNameRequest);
        verifyInvalidParam(create_blankColumnNameResponse, "columnName", "must not be null or blank");

        //Create - Incorrect column amount
        CreateTableRequest create_incorrectColumnAmountRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(COLUMN_VALUE, COLUMN_VALUE)))
            .build();
        Response create_incorrectColumnAmountResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_incorrectColumnAmountRequest);
        verifyInvalidParam(create_incorrectColumnAmountResponse, "columns", "amount different");

        //Create - Null column value
        CreateTableRequest create_nullColumnValueRequest = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList((String) null)))
            .build();
        Response create_nullColumnValueResponse = NotebookActions.getCreateTableResponse(language, accessTokenId, create_nullColumnValueRequest);
        verifyInvalidParam(create_nullColumnValueResponse, "columnValue", "must not be null");

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
        verifyListItemNotFound(get_listItemNotFoundResponse);

        //Edit - Blank title
        EditTableRequest edit_blankTitleRequest = EditTableRequest.builder()
            .title(" ")
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
            .build();
        Response edit_blankTitleResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_blankTitleRequest);
        verifyInvalidParam(edit_blankTitleResponse, "title", "must not be null or blank");

        //Edit - Blank column name
        EditTableRequest edit_blankColumnNameRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(" ")
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
            .build();
        Response edit_blankColumnNameResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_blankColumnNameRequest);
        verifyInvalidParam(edit_blankColumnNameResponse, "columnName", "must not be null or blank");

        /*
        //Edit - Different column amount
        EditTableRequest edit_differentColumnAmountRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build(),
                EditTableJoinRequest.builder()
                    .rowIndex(0)
                    .columnIndex(1)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
            .build();
        Response edit_differentColumnAmountResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_differentColumnAmountRequest);
        verifyInvalidParam(edit_differentColumnAmountResponse, "columns", "amount different");
*/
        //Edit - Null column value
        EditTableRequest edit_nullColumnValueRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(null)
                    .build()
            ))
            .build();
        Response edit_nullColumnValueResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_nullColumnValueRequest);
        verifyInvalidParam(edit_nullColumnValueResponse, "columnValue", "must not be null");

        //Edit - ColumnHead not found
        EditTableRequest edit_columnHeadNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(UUID.randomUUID())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
            .build();
        Response edit_columnHeadNotFoundResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_columnHeadNotFoundRequest);
        verifyListItemNotFound(edit_columnHeadNotFoundResponse);

        //Edit - TableJoin not found
        EditTableRequest edit_tableJoinNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(UUID.randomUUID())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
            .build();
        Response edit_tableJoinNotFoundResponse = NotebookActions.getEditTableResponse(language, accessTokenId, listItemId, edit_tableJoinNotFoundRequest);
        verifyListItemNotFound(edit_tableJoinNotFoundResponse);

        //Edit - ListItem not found
        EditTableRequest edit_listItemNotFoundRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
            .build();
        Response edit_listItemNotFoundResponse = NotebookActions.getEditTableResponse(language, accessTokenId, UUID.randomUUID(), edit_listItemNotFoundRequest);
        verifyListItemNotFound(edit_listItemNotFoundResponse);

        //Edit - Column deleted
        EditTableRequest edit_columnDeletedRequest = EditTableRequest.builder()
            .title(NEW_TITLE)
            .tableHeads(Collections.emptyList())
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
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .columnIndex(0)
                        .columnName(COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(COLUMN_VALUE)
                    .build()
            ))
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
            .tableHeads(
                List.of(
                    EditTableHeadRequest.builder()
                        .tableHeadId(tableResponse.getTableHeads().get(0).getTableHeadId())
                        .columnIndex(0)
                        .columnName(NEW_COLUMN_NAME)
                        .build()
                )
            )
            .columns(List.of(
                EditTableJoinRequest.builder()
                    .tableJoinId(tableResponse.getTableColumns().get(0).getTableJoinId())
                    .rowIndex(0)
                    .columnIndex(0)
                    .content(NEW_COLUMN_VALUE)
                    .build()
            ))
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
}
