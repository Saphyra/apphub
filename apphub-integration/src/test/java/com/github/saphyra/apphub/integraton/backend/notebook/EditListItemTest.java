package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyListItemNotFound;


public class EditListItemTest extends BackEndTest {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String NEW_TITLE = "new-title";
    private static final String ORIGINAL_URL = "original-url";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "notebook"})
    public void editListITem(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parentCategoryId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(ORIGINAL_TITLE).build());

        //Blank title
        EditListItemRequest blankTitleRequest = EditListItemRequest.builder()
            .title(" ")
            .build();
        Response blankTitleResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, blankTitleRequest, parentCategoryId);
        verifyInvalidParam(language, blankTitleResponse, "title", "must not be null or blank");

        //New parent not found
        EditListItemRequest newParentNotFoundRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(UUID.randomUUID())
            .build();
        Response newParentNotFoundResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, newParentNotFoundRequest, parentCategoryId);
        verifyErrorResponse(language, newParentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Parent not category
        CreateLinkRequest createLinkRequest = CreateLinkRequest.builder()
            .parent(null)
            .title(ORIGINAL_TITLE)
            .url(ORIGINAL_URL)
            .build();
        UUID linkId = NotebookActions.createLink(language, accessTokenId, createLinkRequest);

        EditListItemRequest parentNotCategoryRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(linkId)
            .build();
        Response parentNotCategoryResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, parentNotCategoryRequest, parentCategoryId);
        verifyErrorResponse(language, parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //ListItem not found
        EditListItemRequest listItemNotFoundRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .build();
        Response listItemNotFoundResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(language, listItemNotFoundResponse);
    }
}
