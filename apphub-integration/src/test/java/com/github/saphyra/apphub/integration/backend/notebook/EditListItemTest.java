package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
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

    @Test(groups = {"be", "notebook"})
    public void editListITem() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID parentCategoryId = CategoryActions.createCategory(getServerPort(), accessToken, CreateCategoryRequest.builder().title(ORIGINAL_TITLE).build());

        blankTitle(accessToken, parentCategoryId);
        tooLongTitle(accessToken, parentCategoryId);
        newParentNotFound(accessToken, parentCategoryId);
        parentNotCategory(accessToken, parentCategoryId);
        listItemNotFound(accessToken);
    }

    private void tooLongTitle(String accessToken, UUID parentCategoryId) {
        EditListItemRequest blankTitleRequest = EditListItemRequest.builder()
            .title("a".repeat(Constants.MAX_LIST_ITEM_TITLE_LENGTH + 1))
            .build();
        Response blankTitleResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessToken, blankTitleRequest, parentCategoryId);
        verifyInvalidParam(blankTitleResponse, "title", "too long");
    }

    private static void blankTitle(String accessToken, UUID parentCategoryId) {
        EditListItemRequest blankTitleRequest = EditListItemRequest.builder()
            .title(" ")
            .build();
        Response blankTitleResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessToken, blankTitleRequest, parentCategoryId);
        verifyInvalidParam(blankTitleResponse, "title", "must not be null or blank");
    }

    private static void newParentNotFound(String accessToken, UUID parentCategoryId) {
        EditListItemRequest newParentNotFoundRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(UUID.randomUUID())
            .build();
        Response newParentNotFoundResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessToken, newParentNotFoundRequest, parentCategoryId);
        verifyErrorResponse(newParentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void parentNotCategory(String accessToken, UUID parentCategoryId) {
        CreateLinkRequest createLinkRequest = CreateLinkRequest.builder()
            .parent(null)
            .title(ORIGINAL_TITLE)
            .url(ORIGINAL_URL)
            .build();
        UUID linkId = LinkActions.createLink(getServerPort(), accessToken, createLinkRequest);

        EditListItemRequest parentNotCategoryRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(linkId)
            .build();
        Response parentNotCategoryResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessToken, parentNotCategoryRequest, parentCategoryId);
        verifyErrorResponse(parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void listItemNotFound(String accessToken) {
        EditListItemRequest listItemNotFoundRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .build();
        Response listItemNotFoundResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessToken, listItemNotFoundRequest, UUID.randomUUID());
        verifyListItemNotFound(listItemNotFoundResponse);
    }
}
