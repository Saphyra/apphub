package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteListItemTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String COLUMN_NAME = "column-name";

    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getDeleteListItemResponse(language, accessTokenId, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);
    }

    @Test
    public void deleteCategoryAndChild() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest parentRequest = CreateCategoryRequest.builder()
            .title(TITLE)
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, parentRequest);

        CreateCategoryRequest childRequest = CreateCategoryRequest.builder()
            .title(TITLE)
            .parent(parentId)
            .build();
        NotebookActions.createCategory(language, accessTokenId, childRequest);

        NotebookActions.deleteListItem(language, accessTokenId, parentId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    @Test
    public void deleteLink() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateLinkRequest request = CreateLinkRequest.builder()
            .title(TITLE)
            .url(CONTENT)
            .build();

        UUID listItemId = NotebookActions.createLink(language, accessTokenId, request);

        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    @Test
    public void deleteText() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest request = CreateTextRequest.builder()
            .title(TITLE)
            .content(CONTENT)
            .build();

        UUID listItemId = NotebookActions.createText(language, accessTokenId, request);

        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    @Test
    public void deleteChecklist() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(0)
                .checked(false)
                .content(CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    @Test
    public void deleteTable() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTableRequest request = CreateTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .columns(Arrays.asList(Arrays.asList(CONTENT)))
            .build();

        UUID listItemId = NotebookActions.createTable(language, accessTokenId, request);

        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }

    @Test
    public void deleteChecklistTable() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistTableRequest request = CreateChecklistTableRequest.builder()
            .title(TITLE)
            .columnNames(Arrays.asList(COLUMN_NAME))
            .rows(Arrays.asList(ChecklistTableRowRequest.<String>builder().checked(true).columns(Arrays.asList(CONTENT)).build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistTable(language, accessTokenId, request);

        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        List<CategoryTreeView> categoryTreeViews = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTreeViews).isEmpty();
    }
}
