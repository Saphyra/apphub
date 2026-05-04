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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        create_blankTitle(accessToken);
        create_parentNotCategory(accessToken);
        create_parentNotFound(accessToken);
        create_nullUrl(accessToken);
        UUID parentId = CategoryActions.createCategory(getServerPort(), accessToken, CreateCategoryRequest.builder().title(PARENT_TITLE).build());
        UUID linkId = create(accessToken, parentId);
        editLink(accessToken, parentId, linkId);
        delete(accessToken, parentId, linkId);
    }

    private static void create_blankTitle(String accessToken) {
        CreateLinkRequest create_blankTitleRequest = CreateLinkRequest.builder()
            .title(" ")
            .url(URL)
            .build();
        Response create_blankTitleResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessToken, create_blankTitleRequest);
        verifyInvalidParam(create_blankTitleResponse, "title", "must not be null or blank");
    }

    private static void create_parentNotCategory(String accessToken) {
        UUID notCategoryParentId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title("pt").content("pc").build());
        CreateLinkRequest create_parentNotCategoryRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(notCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessToken, create_parentNotCategoryRequest);
        verifyErrorResponse(create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);
    }

    private static void create_parentNotFound(String accessToken) {
        CreateLinkRequest create_parentNotFoundRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessToken, create_parentNotFoundRequest);
        verifyErrorResponse(create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);
    }

    private static void create_nullUrl(String accessToken) {
        CreateLinkRequest create_nullUrlRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(null)
            .build();
        Response create_nullUrlResponse = LinkActions.getCreateLinkResponse(getServerPort(), accessToken, create_nullUrlRequest);
        verifyInvalidParam(create_nullUrlResponse, "url", "must not be null");
    }

    private static UUID create(String accessToken, UUID parentId) {
        CreateLinkRequest createRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(parentId)
            .build();
        UUID linkId = LinkActions.createLink(getServerPort(), accessToken, createRequest);
        ChildrenOfCategoryResponse childrenOfCategoryResponse = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, parentId);
        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView view = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(view.getId()).isEqualTo(linkId);
        assertThat(view.getTitle()).isEqualTo(TITLE);
        assertThat(view.getValue()).isEqualTo(URL);
        assertThat(view.getType()).isEqualTo(ListItemType.LINK.name());
        return linkId;
    }

    private static void editLink(String accessToken, UUID parentId, UUID linkId) {
        EditListItemRequest editLinkRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .value(NEW_URL)
            .parent(parentId)
            .build();
        ListItemActions.editListItem(getServerPort(), accessToken, editLinkRequest, linkId);
        ChildrenOfCategoryResponse childrenOfLinksParentResponse = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, parentId);
        assertThat(childrenOfLinksParentResponse.getChildren()).hasSize(1);
        NotebookView linkView = childrenOfLinksParentResponse.getChildren().get(0);
        assertThat(linkView.getValue()).isEqualTo(NEW_URL);
        assertThat(linkView.getTitle()).isEqualTo(NEW_TITLE);
    }

    private static void delete(String accessToken, UUID parentId, UUID linkId) {
        ListItemActions.deleteListItem(getServerPort(), accessToken, linkId);
        assertThat(CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, parentId).getChildren()).isEmpty();
    }
}
