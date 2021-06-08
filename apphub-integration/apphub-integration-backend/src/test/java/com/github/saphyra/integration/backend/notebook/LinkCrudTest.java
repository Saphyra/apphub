package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChildrenOfCategoryResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.NotebookView;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.CATEGORY_NOT_FOUND;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_TYPE;
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
        verifyInvalidType(language, create_parentNotCategoryResponse);

        //Create - Parent not found
        CreateLinkRequest create_parentNotFoundRequest = CreateLinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(UUID.randomUUID())
            .build();
        Response create_parentNotFoundResponse = NotebookActions.getCreateLinkResponse(language, accessTokenId, create_parentNotFoundRequest);
        verifyCategoryNotFound(language, create_parentNotFoundResponse);

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

    private void verifyCategoryNotFound(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, CATEGORY_NOT_FOUND));
    }

    private void verifyInvalidType(Language language, Response response) {
        assertThat(response.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    private void verifyInvalidParam(Language language, Response response, String title, String s) {
        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get(title)).isEqualTo(s);
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }
}
