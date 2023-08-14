package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleyRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class OnlyTitleCrudTest extends BackEndTest {
    private static final String TITLE = "title";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "notebook"})
    public void onlyTitleCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - Empty title
        CreateOnlyTitleyRequest create_emptyTitleRequest = CreateOnlyTitleyRequest.builder()
            .title(" ")
            .build();
        Response create_emptyTitleResponse = NotebookActions.getCreateOnlyTitleResponse(language, accessTokenId, create_emptyTitleRequest);
        verifyInvalidParam(language, create_emptyTitleResponse, "title", "must not be null or blank");

        //Create - Parent not found
        CreateOnlyTitleyRequest create_parentNotFoundRequest = CreateOnlyTitleyRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateOnlyTitleResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(language, create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create
        CreateOnlyTitleyRequest createRequest = CreateOnlyTitleyRequest.builder()
            .title(TITLE)
            .build();
        UUID listItemId = NotebookActions.createOnlyTitle(language, accessTokenId, createRequest);

        List<NotebookView> content = NotebookActions.getChildrenOfCategory(language, accessTokenId, null)
            .getChildren();
        assertThat(content).hasSize(1);
        assertThat(content.get(0).getId()).isEqualTo(listItemId);
        assertThat(content.get(0).getTitle()).isEqualTo(TITLE);
        assertThat(content.get(0).getType()).isEqualTo(ListItemType.ONLY_TITLE.name());

        //Create - Parent not category
        UUID noCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        CreateOnlyTitleyRequest create_parentNotCategoryRequest = CreateOnlyTitleyRequest.builder()
            .title(TITLE)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateOnlyTitleResponse(language, accessTokenId, create_parentNotCategoryRequest);

        verifyErrorResponse(language, create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //Delete
        NotebookActions.deleteListItem(language, accessTokenId, listItemId);

        content = NotebookActions.getChildrenOfCategory(language, accessTokenId, null)
            .getChildren();
        assertThat(content).hasSize(1);
    }
}
