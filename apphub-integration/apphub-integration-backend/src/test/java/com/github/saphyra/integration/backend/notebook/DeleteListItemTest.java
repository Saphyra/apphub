package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteListItemTest extends TestBase {
    private static final String TITLE = "title";

    @DataProvider(name = "localeDataProvider", parallel = true)
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
    public void deleteLink(){
        //TODO implement
    }

    @Test
    public void deleteText(){
        //TODO implement
    }

    @Test
    public void deleteChecklist(){
        //TODO implement
    }

    @Test
    public void deleteTable(){
        //TODO implement
    }
}
