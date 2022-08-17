package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.structure.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.notebook.ListItemType;
import com.github.saphyra.apphub.integration.structure.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
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

    @Test(dataProvider = "languageDataProvider")
    public void linkCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Create - Blank title
        CreateLinkRequest create_blankTitleRequest = CreateLinkRequest.builder()
            .title(" ")
            .url(URL)
            .build();
        Response create_blankTitleResponse = NotebookActions.getCreateLinkResponse(language, accessTokenId, create_blankTitleRequest);
        verifyInvalidParam(language, create_blankTitleResponse, "title", "must not be null or blank");

        //Create - Parent not category
        UUID notCategoryParentId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title("pt").content("pc").build());
        CreateLinkRequest create_parentNotCategoryRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(notCategoryParentId)
            .build();
        Response create_parentNotCategoryResponse = NotebookActions.getCreateLinkResponse(language, accessTokenId, create_parentNotCategoryRequest);
        verifyErrorResponse(language, create_parentNotCategoryResponse, 422, ErrorCode.INVALID_TYPE);

        //Create - Parent not found
        CreateLinkRequest create_parentNotFoundRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateLinkResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyErrorResponse(language, create_parentNotFoundResponse, 404, ErrorCode.CATEGORY_NOT_FOUND);

        //Create - Null URL
        CreateLinkRequest create_nullUrlRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(null)
            .build();
        Response create_nullUrlResponse = NotebookActions.getCreateLinkResponse(language, accessTokenId, create_nullUrlRequest);
        verifyInvalidParam(language, create_nullUrlResponse, "url", "must not be null");

        //Create
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, CreateCategoryRequest.builder().title(PARENT_TITLE).build());
        CreateLinkRequest createRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(parentId)
            .build();
        UUID linkId = NotebookActions.createLink(language, accessTokenId, createRequest);
        ChildrenOfCategoryResponse childrenOfCategoryResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId);
        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView view = childrenOfCategoryResponse.getChildren().get(0);
        assertThat(view.getId()).isEqualTo(linkId);
        assertThat(view.getTitle()).isEqualTo(TITLE);
        assertThat(view.getValue()).isEqualTo(URL);
        assertThat(view.getType()).isEqualTo(ListItemType.LINK.name());

        //Edit link
        EditListItemRequest editLinkRequest = EditListItemRequest.builder()
            .title(NEW_TITLE)
            .value(NEW_URL)
            .parent(parentId)
            .build();
        NotebookActions.editListItem(language, accessTokenId, editLinkRequest, linkId);
        ChildrenOfCategoryResponse childrenOfLinksParentResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId);
        assertThat(childrenOfLinksParentResponse.getChildren()).hasSize(1);
        NotebookView linkView = childrenOfLinksParentResponse.getChildren().get(0);
        assertThat(linkView.getValue()).isEqualTo(NEW_URL);
        assertThat(linkView.getTitle()).isEqualTo(NEW_TITLE);

        //Delete
        NotebookActions.deleteListItem(language, accessTokenId, linkId);
        assertThat(NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId).getChildren()).isEmpty();
    }
}
