package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.ColumnType;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableColumnModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableHeadModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.TableRowModel;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class SearchListItemTest extends BackEndTest {
    private static final String CATEGORY_TITLE = "category-title";
    private static final String LINK_TITLE = "link-title";
    private static final String LINK_URL = "link-url";
    private static final String TEXT_CONTENT = "text-content";
    private static final String CHECKLIST_ITEM_CONTENT = "checklist-item-content";
    private static final String TABLE_COLUMN_NAME = "table-column-name";
    private static final String TABLE_COLUMN_VALUE = "table-column-value";
    private static final String TEXT_TITLE = "text-title";
    private static final String CHECKLIST_TITLE = "checklist-title";
    private static final String TABLE_TITLE = "table-title";
    private static final String CHECKLIST_TABLE_TITLE = "checklist-table-title";
    private static final String CHECKLIST_TABLE_COLUMN_NAME = "checklist-table-column-name";
    private static final String CHECKLIST_TABLE_COLUMN_VALUE = "checklist-table-column-value";

    @Test(groups = {"be", "notebook"})
    public void search() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        UUID categoryId = CategoryActions.createCategory(accessTokenId, CreateCategoryRequest.builder().title(CATEGORY_TITLE).build());

        LinkActions.createLink(accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).parent(categoryId).url(LINK_URL).build());
        TextActions.createText(accessTokenId, CreateTextRequest.builder().title(TEXT_TITLE).content(TEXT_CONTENT).build());
        ChecklistActions.createChecklist(
            accessTokenId,
            CreateChecklistRequest.builder()
                .title(CHECKLIST_TITLE)
                .items(Arrays.asList(ChecklistItemModel.builder()
                    .index(0)
                    .checked(true)
                    .content(CHECKLIST_ITEM_CONTENT)
                    .build()))
                .build()
        );
        TableActions.createTable(
            accessTokenId,
            CreateTableRequest.builder()
                .title(TABLE_TITLE)
                .listItemType(ListItemType.TABLE)
                .tableHeads(List.of(TableHeadModel.builder()
                    .columnIndex(0)
                    .content(TABLE_COLUMN_NAME)
                    .build()))
                .rows(List.of(TableRowModel.builder()
                    .rowIndex(0)
                    .columns(List.of(TableColumnModel.builder()
                        .columnIndex(0)
                        .columnType(ColumnType.TEXT)
                        .data(TABLE_COLUMN_VALUE)
                        .build()))
                    .build()))
                .build()
        );

        TableActions.createTable(
            accessTokenId,
            CreateTableRequest.builder()
                .title(CHECKLIST_TABLE_TITLE)
                .listItemType(ListItemType.CHECKLIST_TABLE)
                .tableHeads(List.of(TableHeadModel.builder()
                    .columnIndex(0)
                    .content(CHECKLIST_TABLE_COLUMN_NAME)
                    .build()))
                .rows(List.of(TableRowModel.builder()
                    .rowIndex(0)
                    .checked(true)
                    .columns(List.of(TableColumnModel.builder()
                        .columnIndex(0)
                        .columnType(ColumnType.TEXT)
                        .data(CHECKLIST_TABLE_COLUMN_VALUE)
                        .build()))
                    .build()))
                .build()
        );

        searchTextTooShort(accessTokenId);

        search(accessTokenId, CATEGORY_TITLE, CATEGORY_TITLE, ListItemType.CATEGORY);
        search(accessTokenId, LINK_TITLE, LINK_TITLE, ListItemType.LINK);
        search(accessTokenId, LINK_URL, LINK_TITLE, ListItemType.LINK);
        search(accessTokenId, TEXT_TITLE, TEXT_TITLE, ListItemType.TEXT);
        search(accessTokenId, TEXT_CONTENT, TEXT_TITLE, ListItemType.TEXT);
        search(accessTokenId, CHECKLIST_TITLE, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(accessTokenId, CHECKLIST_ITEM_CONTENT, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(accessTokenId, TABLE_TITLE, TABLE_TITLE, ListItemType.TABLE);
        search(accessTokenId, TABLE_COLUMN_NAME, TABLE_TITLE, ListItemType.TABLE);
        search(accessTokenId, TABLE_COLUMN_VALUE, TABLE_TITLE, ListItemType.TABLE);
        search(accessTokenId, CHECKLIST_TABLE_TITLE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(accessTokenId, CHECKLIST_TABLE_COLUMN_NAME, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(accessTokenId, CHECKLIST_TABLE_COLUMN_VALUE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
    }

    private static void searchTextTooShort(UUID accessTokenId) {
        Response response = ListItemActions.getSearchResponse(accessTokenId, "as");
        ResponseValidator.verifyInvalidParam(response, "search", "too short");
    }

    @Test(groups = {"be", "notebook"})
    public void sameItemShouldBeReturnedOnlyOnce() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        LinkActions.createLink(accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).url(LINK_TITLE).build());

        List<NotebookView> searchResult = ListItemActions.search(accessTokenId, LINK_TITLE);

        assertThat(searchResult).hasSize(1);
    }

    private void search(UUID accessTokenId, String search, String listItemTitle, ListItemType type) {
        List<NotebookView> searchResult = ListItemActions.search(accessTokenId, search);

        NotebookView expected = searchResult.stream()
            .filter(notebookView -> notebookView.getTitle().equals(listItemTitle))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ListItem not found in list for searchText " + search + " with title " + listItemTitle));

        assertThat(expected.getType()).isEqualTo(type.name());
    }
}
