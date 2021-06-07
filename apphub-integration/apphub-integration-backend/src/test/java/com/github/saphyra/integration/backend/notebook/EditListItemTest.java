package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.CATEGORY_NOT_FOUND;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_TYPE;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class EditListItemTest extends BackEndTest {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String NEW_TITLE = "new-title";
    private static final String ORIGINAL_URL = "original-url";
    private static final String NEW_URL = "new-url";

    @Test(dataProvider = "languageDataProvider")
    public void editListITem(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID parentCategoryId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(ORIGINAL_TITLE).build());

        //Blank title
        EditListItemRequest blankTitleRequest = EditListItemRequest.builder()
            .title(" ")
            .build();
        Response blankTitleResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, blankTitleRequest, parentCategoryId);
        verifyInvalidParam(language, blankTitleResponse);

        //New parent not found
        EditListItemRequest newParentNotFoundRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(UUID.randomUUID())
            .build();
        Response newParentNotFoundResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, newParentNotFoundRequest, parentCategoryId);
        verifyCategoryNotFound(language, newParentNotFoundResponse);

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
        verifyInvalidType(language, parentNotCategoryResponse);

        //ListItem not found
        EditListItemRequest listItemNotFoundRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .build();
        Response listItemNotFoundResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(language, listItemNotFoundResponse);
    }

    private void verifyInvalidParam(Language language, Response ownChildResponse) {
        assertThat(ownChildResponse.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = ownChildResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    private void verifyListItemNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
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
}
