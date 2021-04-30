package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
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

import java.util.Arrays;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CreateChecklistTest extends TestBase {
    private static final Integer ORDER = 234;
    private static final String CONTENT = "content";
    private static final String TITLE = "title";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(" ")
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void parentNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(UUID.randomUUID())
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

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
            .title("a")
            .content("")
            .build();
        UUID parent = NotebookActions.createText(language, accessTokenId, createTextRequest);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .parent(parent)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(422);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_TYPE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_TYPE));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullNodes(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(null)
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("nodes")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullContent(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(null)
                .build()))
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("node.content")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullChecked(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(null)
                .content(CONTENT)
                .build()))
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("node.checked")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullOrder(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(null)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        Response response = NotebookActions.getCreateChecklistItemResponse(language, accessTokenId, request);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("node.order")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test
    public void createChecklistItem() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(ORDER)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        ChecklistResponse response = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        assertThat(response.getTitle()).isEqualTo(TITLE);
        assertThat(response.getNodes()).hasSize(1);
        assertThat(response.getNodes().get(0).getChecklistItemId()).isNotNull();
        assertThat(response.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(response.getNodes().get(0).getChecked()).isTrue();
        assertThat(response.getNodes().get(0).getOrder()).isEqualTo(ORDER);
    }
}
