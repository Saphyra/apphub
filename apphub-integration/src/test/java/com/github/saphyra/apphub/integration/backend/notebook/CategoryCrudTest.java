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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_emptyTitle(accessToken);
        UUID parentCategoryId = create_addToRoot(accessToken);
        create_parentNotFound(accessToken);
        create_parentNotCategory(accessToken);
        create(accessToken, parentCategoryId);
        UUID childId = edit_ownChild(accessToken, parentCategoryId);
        edit(accessToken, parentCategoryId, childId);
        delete(accessToken, parentCategoryId);
    }

    private static void create_emptyTitle(String accessToken) {
        CreateCategoryRequest create_emptyTitleRequest = CreateCategoryRequest.builder()
            .title(" ")
            .build();
        Response create_emptyTitleResponse = CategoryActions.getCreateCategoryResponse(getServerPort(), accessToken, create_emptyTitleRequest);
        verifyInvalidParam(create_emptyTitleResponse, "title", "must not be null or blank");
    }

    private static UUID create_addToRoot(String accessToken) {
        CreateCategoryRequest create_addToRootRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .build();
        UUID parentCategoryId = CategoryActions.createCategory(getServerPort(), accessToken, create_addToRootRequest);

        List<CategoryTreeView> categories = CategoryActions.getCategoryTree(getServerPort(), accessToken);
        assertThat(categories).hasSize(1);
        assertThat(categories.get(0).getCategoryId()).isEqualTo(parentCategoryId);
        assertThat(categories.get(0).getTitle()).isEqualTo(TITLE_1);
        assertThat(categories.get(0).getChildren()).isEmpty();
        return parentCategoryId;
    }

    private static void create_parentNotFound(String accessToken) {
        CreateCategoryRequest create_parentNotFoundRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = CategoryActions.getCreateCategoryResponse(getServerPort(), accessToken, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_parentNotCategory(String accessToken) {
        UUID noCategoryParentId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title(TITLE_1).content("").build());
        CreateCategoryRequest create_parentNotCategoryRequest = CreateCategoryRequest.builder()
            .title(TITLE_1)
            .parent(noCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = CategoryActions.getCreateCategoryResponse(getServerPort(), accessToken, create_parentNotCategoryRequest);

        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create(String accessToken, UUID parentCategoryId) {
        CreateCategoryRequest createRequest = CreateCategoryRequest.builder()
            .title(TITLE_2)
            .parent(parentCategoryId)
            .build();
        UUID childCategoryId = CategoryActions.createCategory(getServerPort(), accessToken, createRequest);

        List<CategoryTreeView> categoryTree = CategoryActions.getCategoryTree(getServerPort(), accessToken);
        assertThat(categoryTree).hasSize(1);
        assertThat(categoryTree.get(0).getChildren()).hasSize(1);
        assertThat(categoryTree.get(0).getChildren().get(0).getCategoryId()).isEqualTo(childCategoryId);
        assertThat(categoryTree.get(0).getChildren().get(0).getTitle()).isEqualTo(TITLE_2);
        assertThat(categoryTree.get(0).getChildren().get(0).getChildren()).isEmpty();
    }

    private static UUID edit_ownChild(String accessToken, UUID parentCategoryId) {
        CreateCategoryRequest createChildRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(parentCategoryId)
            .build();
        UUID childId = CategoryActions.createCategory(getServerPort(), accessToken, createChildRequest);
        EditListItemRequest ownChildRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .build();
        Response ownChildResponse = ListItemActions.getEditListItemResponse(getServerPort(), accessToken, ownChildRequest, parentCategoryId);
        verifyErrorResponse(ownChildResponse, 400, ErrorCode.INVALID_PARAM, "parent", "must not be own child");
        return childId;
    }

    private static void edit(String accessToken, UUID parentCategoryId, UUID childId) {
        CreateCategoryRequest createModifiedCategoryRequest = CreateCategoryRequest.builder()
            .title("asd")
            .parent(parentCategoryId)
            .build();
        UUID modifiedCategoryId = CategoryActions.createCategory(getServerPort(), accessToken, createModifiedCategoryRequest);
        EditListItemRequest editListItemRequest = EditListItemRequest.builder()
            .parent(childId)
            .title(NEW_TITLE)
            .build();
        ListItemActions.editListItem(getServerPort(), accessToken, editListItemRequest, modifiedCategoryId);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, childId);
        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView categoryView = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(categoryView.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static void delete(String accessToken, UUID parentCategoryId) {
        ListItemActions.deleteListItem(getServerPort(), accessToken, parentCategoryId);
        assertThat(CategoryActions.getCategoryTree(getServerPort(), accessToken)).isEmpty();
    }
}
