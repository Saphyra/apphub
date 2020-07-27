package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.*;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.ListItemType;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateLinkTest extends TestBase {
    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String PARENT_TITLE = "parent-title";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        LinkRequest request = LinkRequest.builder()
            .title(" ")
            .url(URL)
            .build();

        Response response = NotebookActions.getCreateLinkResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(UUID.randomUUID())
            .build();

        Response response = NotebookActions.getCreateLinkResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.CATEGORY_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_CATEGORY_NOT_FOUND));
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotCategory(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest parentRequest = CreateTextRequest.builder()
            .title("pt")
            .content("pc")
            .build();
        UUID parentId = NotebookActions.createText(language, accessTokenId, parentRequest);

        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(parentId)
            .build();
        Response response = NotebookActions.getCreateLinkResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_TYPE));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullUrl(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .url(null)
            .build();

        Response response = NotebookActions.getCreateLinkResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("url")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, ERROR_CODE_INVALID_PARAM));
    }

    @Test
    public void createLink() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateCategoryRequest createCategoryRequest = CreateCategoryRequest.builder()
            .title(PARENT_TITLE)
            .build();
        UUID parentId = NotebookActions.createCategory(language, accessTokenId, createCategoryRequest);

        LinkRequest request = LinkRequest.builder()
            .title(TITLE)
            .url(URL)
            .parent(parentId)
            .build();
        UUID textId = NotebookActions.createLink(language, accessTokenId, request);

        ChildrenOfCategoryResponse childrenOfCategoryResponse = NotebookActions.getChildrenOfCategory(language, accessTokenId, parentId);

        assertThat(childrenOfCategoryResponse.getChildren()).hasSize(1);
        NotebookView view = childrenOfCategoryResponse.getChildren().get(0);

        assertThat(view.getId()).isEqualTo(textId);
        assertThat(view.getTitle()).isEqualTo(TITLE);
        assertThat(view.getValue()).isEqualTo(URL);
        assertThat(view.getType()).isEqualTo(ListItemType.LINK.name());
    }
}
