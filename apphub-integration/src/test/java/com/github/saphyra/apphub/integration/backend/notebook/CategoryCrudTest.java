package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
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

    @Test(groups = {"be", "notebook"})
    public void categoryCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_emptyTitle(accessTokenId);
        UUID parentCategoryId = create_addToRoot(accessTokenId);
        create_parentNotFound(accessTokenId);
        create_parentNotCategory(accessTokenId);
        create(accessTokenId, parentCategoryId);
        UUID childId = edit_ownChild(accessTokenId, parentCategoryId);
        edit(accessTokenId, parentCategoryId, childId);
        delete(accessTokenId, parentCategoryId);
    }

    private static void create_emptyTitle(UUID accessTokenId) {
        CreateCategoryRequest create_emptyTitleRequest = CreateCategoryRequest.builder()
            .title(" ")
            .build();
        Response create_emptyTitleResponse = CategoryActions.getCreateCategoryResponse(getServerPort(), accessTokenId, create_emptyTitleRequest);
        verifyInvalidParam(create_emptyTitleResponse, "title", "must not be null or blank");
    }

    private static UUID create_addToRoot(UUID accessTokenId) {
        CreateCategoryRequest create_addToRootRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, create_addToRootRequest);

        List<CategoryTreeView> categories = CategoryActions.getCategoryTree(getServerPort(), accessTokenId);
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getCategoryId()).isEqualTo(parentCategoryId);
        assertThat(categories.get(0).getTitle()).isEqualTo(TITLE_1);
        assertThat(categories.get(0).getChildren()).isEmpty();
        return parentCategoryId;
    }

    private static void create_parentNotFound(UUID accessTokenId) {
        CreateCategoryRequest create_parentNotFoundRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = CategoryActions.getCreateCategoryResponse(getServerPort(), accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_parentNotCategory(UUID accessTokenId) {
        UUID noCategoryParentId = TextActions.createText(getServerPort(), accessTokenId, CreateTextRequest.builder().title(TITLE_1).content("").build());
        CreateCategoryRequest create_parentNotCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = CategoryActions.getCreateCategoryResponse(getServerPort(), accessTokenId, create_parentNotCategoryRequest);

        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create(UUID accessTokenId, UUID parentCategoryId) {
        CreateCategoryRequest createRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentCategoryId)
            .build();
        UUID childCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, createRequest);

        List<CategoryTreeView> categoryTree = CategoryActions.getCategoryTree(getServerPort(), accessTokenId);
        assertThat(categoryTree).hasSize(1);
        assertThat(categoryTree.get(0).getChildren()).hasSize(1);
        assertThat(categoryTree.get(0).getChildren().get(0).getCategoryId()).isEqualTo(childCategoryId);
        assertThat(categoryTree.get(0).getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(categoryTree.get(0).getChildren().get(0).getChildren()).isEmpty();
    }

    private static UUID edit_ownChild(UUID accessTokenId, UUID parentCategoryId) {
        CreateCategoryRequest createChildRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(parentCategoryId)
            .build();
        UUID childId = CategoryActions.createCategory(getServerPort(), accessTokenId, createChildRequest);
        EditListItemRequest ownChildRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .build();
        Response ownChildResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessTokenId, ownChildRequest, parentCategoryId);
        verifyErrorResponse(ownChildResponse, 400, ErrorCode.INVALID_PARAM, "parent", "must not be own child");
        return childId;
    }

    private static void edit(UUID accessTokenId, UUID parentCategoryId, UUID childId) {
        CreateCategoryRequest createModifiedCategoryRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(parentCategoryId)
            .build();
        UUID modifiedCategoryId = CategoryActions.createCategory(getServerPort(), accessTokenId, createModifiedCategoryRequest);
        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .build();
        ListItemActions.editListItem(getServerPort(), accessTokenId, editListItemRequest, modifiedCategoryId);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, childId);
        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView categoryView = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(categoryView.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static void delete(UUID accessTokenId, UUID parentCategoryId) {
        ListItemActions.deleteListItem(getServerPort(), accessTokenId, parentCategoryId);
        assertThat(CategoryActions.getCategoryTree(getServerPort(), accessTokenId)).isEmpty();
    }
}
