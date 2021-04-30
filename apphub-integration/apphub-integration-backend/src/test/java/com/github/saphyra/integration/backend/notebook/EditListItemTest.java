package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.*;
import static org.assertj.core.api.Assertions.assertThat;

public class EditListItemTest extends TestBase {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String NEW_TITLE = "new-title";
    private static final String ORIGINAL_URL = "original-url";
    private static final String NEW_URL = "new-url";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(ORIGINAL_TITLE)
            .build();
        UUID listItemId = NotebookActions.createCategory(language, accessTokenId, request);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .title(" ")
            .build();

        Response response = NotebookActions.getEditListItemResponse(language, accessTokenId, editListItemRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(ORIGINAL_TITLE)
            .build();
        UUID listItemId = NotebookActions.createCategory(language, accessTokenId, request);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(UUID.randomUUID())
            .build();

        Response response = NotebookActions.getEditListItemResponse(language, accessTokenId, editListItemRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, CATEGORY_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotCategory(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest createTextRequest = CreateTextRequest.builder()
            .content("asd")
            .title("asd")
            .build();
        UUID parent = NotebookActions.createText(language, accessTokenId, createTextRequest);

        CreateCategoryRequest request = CreateCategoryRequest.builder()
            .title(ORIGINAL_TITLE)
            .build();
        UUID listItemId = NotebookActions.createCategory(language, accessTokenId, request);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .parent(parent)
            .build();

        Response response = NotebookActions.getEditListItemResponse(language, accessTokenId, editListItemRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(422);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .build();

        Response response = NotebookActions.getEditListItemResponse(language, accessTokenId, editListItemRequest, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void editLink() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title("asd")
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateLinkRequest createLinkRequest = CreateLinkRequest.builder()
            .parent(null)
            .title(ORIGINAL_TITLE)
            .url(ORIGINAL_URL)
            .build();
        UUID listItemId = NotebookActions.createLink(language, accessTokenId, createLinkRequest);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(parentId)
            .title(NEW_TITLE)
            .value(NEW_URL)
            .build();
        NotebookActions.editListItem(language, accessTokenId, editListItemRequest, listItemId);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId);

        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView notebookView = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(notebookView.getValue()).isEqualTo(NEW_URL);
        assertThat(notebookView.getTitle()).isEqualTo(NEW_TITLE);
    }

    @Test(dataProvider = "localeDataProvider")
    public void editCategory_ownChild(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title(ORIGINAL_TITLE)
            .build();
        UUID listItemId = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateCategoryRequest createChildRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(listItemId)
            .build();
        UUID childId = NotebookActions.createCategory(language, accessTokenId, createChildRequest);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .value(NEW_URL)
            .build();
        Response response = NotebookActions.getEditListItemResponse(language, accessTokenId, editListItemRequest, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(422);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get("parent")).isEqualTo("must not be own child");
    }

    @Test
    public void editCategory() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title(ORIGINAL_TITLE)
            .build();
        UUID listItemId = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        CreateCategoryRequest createParentRequest = CreateCategoryRequest.builder()
            .title("asd")
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, createParentRequest);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(parentId)
            .title(NEW_TITLE)
            .value(NEW_URL)
            .build();
        NotebookActions.editListItem(language, accessTokenId, editListItemRequest, listItemId);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId);

        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView notebookView = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(notebookView.getTitle()).isEqualTo(NEW_TITLE);
    }

    @Test
    public void editListItem() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest createCategoryRequest = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content("ads")
            .build();
        UUID listItemId = NotebookActions.createText(language, accessTokenId, createCategoryRequest);

        CreateCategoryRequest createParentRequest = CreateCategoryRequest.builder()
            .title("asd")
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, createParentRequest);

        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(parentId)
            .title(NEW_TITLE)
            .value(NEW_URL)
            .build();
        NotebookActions.editListItem(language, accessTokenId, editListItemRequest, listItemId);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId);

        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView notebookView = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(notebookView.getTitle()).isEqualTo(NEW_TITLE);
    }
}
