package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.structure.api.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkCrudTest extends BackEndTest {
    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String PARENT_TITLE = "parent-title";
    private static final String NEW_TITLE = "new-title";
    private static final String NEW_URL = "new-url";

    @Test(groups = {"be", "notebook"})
    public void linkCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessTokenId);
        create_parentNotCategory(accessTokenId);
        create_parentNotFound(accessTokenId);
        create_nullUrl(accessTokenId);
        UUID parentId = CategoryActions.createCategory(getServerPort(), accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).build());
        UUID linkId = create(accessTokenId, parentId);
        editLink(accessTokenId, parentId, linkId);
        delete(accessTokenId, parentId, linkId);
    }

    private static void create_blankTitle(UUID accessTokenId) {
        CreateLinkRequest create_blankTitleRequest = CreateLinkRequest.builder()
            .title(" ")
            .url(URL)
            .build();
        Response create_blankTitleResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotCategory(UUID accessTokenId) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessTokenId, CreateTextRequest.builder().title("pt").content("pc").build());
        CreateLinkRequest create_parentNotCategoryRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(notCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create_parentNotFound(UUID accessTokenId) {
        CreateLinkRequest create_parentNotFoundRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_nullUrl(UUID accessTokenId) {
        CreateLinkRequest create_nullUrlRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(null)
            .build();
        Response create_nullUrlResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessTokenId, create_nullUrlRequest);
        verifyInvalidParam(create_nullUrlResponse, "url", "must not be null");
    }

    private static UUID create(UUID accessTokenId, UUID parentId) {
        CreateLinkRequest createRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(parentId)
            .build();
        UUID linkId = LinkActions.createLink(getServerPort(), accessTokenId, createRequest);
        ChildrenOfCategoryResponse childrenOfCategoryResponse = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, parentId);
        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView view = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(view.getId()).isEqualTo(linkId);
        assertThat(view.getTitle()).isEqualTo(TITLE);
        assertThat(view.getValue()).isEqualTo(URL);
        assertThat(view.getType()).isEqualTo(ListItemType.LINK.name());
        return linkId;
    }

    private static void editLink(UUID accessTokenId, UUID parentId, UUID linkId) {
        EditListItemRequest editLinkRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .value(NEW_URL)
            .parent(parentId)
            .build();
        ListItemActions.editListItem(getServerPort(), accessTokenId, editLinkRequest, linkId);
        ChildrenOfCategoryResponse childrenOfLinksParentResponse = CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, parentId);
        assertThat(childrenOfLinksParentResponse.getChildren()).hasSize(1);
        NotebookView linkView = childrenOfLinksParentResponse.getChildren().get(0);
        assertThat(linkView.getValue()).isEqualTo(NEW_URL);
        assertThat(linkView.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static void delete(UUID accessTokenId, UUID parentId, UUID linkId) {
        ListItemActions.deleteListItem(getServerPort(), accessTokenId, linkId);
        assertThat(CategoryActions.getChildrenOfCategory(getServerPort(), accessTokenId, parentId).getChildren()).isEmpty();
    }
}
