package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.notebook.CategoryTreeView;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
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
        verifyInvalidParam(language, create_emptyTitleResponse, "title", "must not be null or blank");

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
        verifyErrorResponse(language, create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create - Parent not category
        UUID noCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE_1).content("").build());
        CreateCategoryRequest create_parentNotCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateCategoryResponse(language, accessTokenId, create_parentNotCategoryRequest);

        verifyErrorResponse(language, create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

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
        verifyErrorResponse(language, ownChildResponse, 400, ErrorCode.INVALID_PARAM, "parent", "must not be own child");

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
}
