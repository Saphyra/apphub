package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.NotebookView;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.CATEGORY_NOT_FOUND;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class CategoryCrudTest extends BackEndTest {
    private static final String TITLE_1 = "title-1";
    private static final String TITLE_2 = "title-2";
    private static final String NEW_TITLE = "new-title";

    @Test(dataProvider = "languageDataProvider")
    public void categoryCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - Empty title
        CreateCategoryRequest create_emptyTitleRequest = CreateCategoryRequest.builder()
            .title(" ")
            .build();
        Response create_emptyTitleResponse = NotebookActions.getCreateCategoryResponse(language, accessTokenId, create_emptyTitleRequest);
        verifyInvalidParam(language, create_emptyTitleResponse);

        //Create - Add to root
        CreateCategoryRequest create_addToRootRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentCategoryId = NotebookActions.createCategory(language, accessTokenId, create_addToRootRequest);

        List<CategoryTreeView> categories = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getCategoryId()).isEqualTo(parentCategoryId);
        assertThat(categories.get(0).getTitle()).isEqualTo(TITLE_1);
        assertThat(categories.get(0).getChildren()).isEmpty();

        //Create - Parent not found
        CreateCategoryRequest create_parentNotFoundRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateCategoryResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyCategoryNotFound(language, create_parentNotFoundResponse);

        //Create - Parent not category
        UUID noCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE_1).content("").build());
        CreateCategoryRequest create_parentNotCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateCategoryResponse(language, accessTokenId, create_parentNotCategoryRequest);

        verifyInvalidType(language, create_parentNotCategoryResponse);

        //Create
        CreateCategoryRequest createRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentCategoryId)
            .build();
        UUID childCategoryId = NotebookActions.createCategory(language, accessTokenId, createRequest);

        List<CategoryTreeView> categoryTree = NotebookActions.getCategoryTree(language, accessTokenId);
        assertThat(categoryTree).hasSize(1);
        assertThat(categoryTree.get(0).getChildren()).hasSize(1);
        assertThat(categoryTree.get(0).getChildren().get(0).getCategoryId()).isEqualTo(childCategoryId);
        assertThat(categoryTree.get(0).getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(categoryTree.get(0).getChildren().get(0).getChildren()).isEmpty();

        //Edit - Own child
        CreateCategoryRequest createChildRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(parentCategoryId)
            .build();
        UUID childId = NotebookActions.createCategory(language, accessTokenId, createChildRequest);
        EditListItemRequest ownChildRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .build();
        Response ownChildResponse = NotebookActions.getEditListItemResponse(language, accessTokenId, ownChildRequest, parentCategoryId);
        verifyInvalidParam(language, ownChildResponse, 422, "parent", "must not be own child");

        //Edit
        CreateCategoryRequest createModifiedCategoryRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(parentCategoryId)
            .build();
        UUID modifiedCategoryId = NotebookActions.createCategory(language, accessTokenId, createModifiedCategoryRequest);
        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .build();
        NotebookActions.editListItem(language, accessTokenId, editListItemRequest, modifiedCategoryId);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, childId);
        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView categoryView = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(categoryView.getTitle()).isEqualTo(NEW_TITLE);

        //Delete
        NotebookActions.deleteListItem(language, accessTokenId, parentCategoryId);
        assertThat(NotebookActions.getCategoryTree(language, accessTokenId)).isEmpty();
    }

    private void verifyInvalidType(Language language, Response create_parentNotCategoryResponse) {
        assertThat(create_parentNotCategoryResponse.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = create_parentNotCategoryResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    private void verifyCategoryNotFound(Language language, Response create_parentNotFoundResponse) {
        assertThat(create_parentNotFoundResponse.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = create_parentNotFoundResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, CATEGORY_NOT_FOUND));
    }

    private void verifyInvalidParam(Language language, Response response) {
        verifyInvalidParam(language, response, 400, "title", "must not be null or blank");
    }

    private void verifyInvalidParam(Language language, Response response, int status, String field, String value) {
        assertThat(response.getStatusCode()).isEqualTo(status);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
        assertThat(errorResponse.getParams().get(field)).isEqualTo(value);
    }
}
