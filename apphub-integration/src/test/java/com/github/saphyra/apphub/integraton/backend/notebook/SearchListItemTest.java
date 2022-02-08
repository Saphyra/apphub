package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.structure.notebook.ChecklistTableRowRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateChecklistTableRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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

    @Test(dataProvider = "languageDataProvider")
    public void searchTextTooShort(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getSearchResponse(language, accessTokenId, "as");

        ResponseValidator.verifyInvalidParam(language, response, "search", "too short");
    }

    @Test
    public void search() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID categoryId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(CATEGORY_TITLE).build());

        NotebookActions.createLink(language, accessTokenId, CreateLinkRequest.builder().title(LINK_TITLE).parent(categoryId).url(LINK_URL).build());
        NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TEXT_TITLE).content(TEXT_CONTENT).build());
        NotebookActions.createChecklist(
            language,
            accessTokenId,
            CreateChecklistItemRequest.builder()
                .title(CHECKLIST_TITLE)
                .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                    .order(0)
                    .checked(true)
                    .content(CHECKLIST_ITEM_CONTENT)
                    .build()))
                .build()
        );
        NotebookActions.createTable(
            language,
            accessTokenId,
            CreateTableRequest.builder()
                .title(TABLE_TITLE)
                .columnNames(Arrays.asList(TABLE_COLUMN_NAME))
                .columns(Arrays.asList(Arrays.asList(TABLE_COLUMN_VALUE)))
                .build()
        );

        NotebookActions.createChecklistTable(
            language,
            accessTokenId,
            CreateChecklistTableRequest.builder()
                .title(CHECKLIST_TABLE_TITLE)
                .columnNames(Arrays.asList(CHECKLIST_TABLE_COLUMN_NAME))
                .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder()
                    .checked(true)
                    .columns(Arrays.asList(CHECKLIST_TABLE_COLUMN_VALUE))
                    .build()))
                .build()
        );

        search(language, accessTokenId, CATEGORY_TITLE, CATEGORY_TITLE, ListItemType.CATEGORY);
        search(language, accessTokenId, LINK_TITLE, LINK_TITLE, ListItemType.LINK);
        search(language, accessTokenId, LINK_URL, LINK_TITLE, ListItemType.LINK);
        search(language, accessTokenId, TEXT_TITLE, TEXT_TITLE, ListItemType.TEXT);
        search(language, accessTokenId, TEXT_CONTENT, TEXT_TITLE, ListItemType.TEXT);
        search(language, accessTokenId, CHECKLIST_TITLE, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(language, accessTokenId, CHECKLIST_ITEM_CONTENT, CHECKLIST_TITLE, ListItemType.CHECKLIST);
        search(language, accessTokenId, TABLE_TITLE, TABLE_TITLE, ListItemType.TABLE);
        search(language, accessTokenId, TABLE_COLUMN_NAME, TABLE_TITLE, ListItemType.TABLE);
        search(language, accessTokenId, TABLE_COLUMN_VALUE, TABLE_TITLE, ListItemType.TABLE);
        search(language, accessTokenId, CHECKLIST_TABLE_TITLE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(language, accessTokenId, CHECKLIST_TABLE_COLUMN_NAME, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
        search(language, accessTokenId, CHECKLIST_TABLE_COLUMN_VALUE, CHECKLIST_TABLE_TITLE, ListItemType.CHECKLIST_TABLE);
    }

    private void search(Language language, UUID accessTokenId, String search, String listItemTitle, ListItemType type) {
        List<NotebookView> searchResult = NotebookActions.search(language, accessTokenId, search);

        NotebookView expected = searchResult.stream()
            .filter(notebookView -> notebookView.getTitle().equals(listItemTitle))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("ListItem not found in list for searchText " + search + " with title " + listItemTitle));

        assertThat(expected.getType()).isEqualTo(type.name());
    }
}
