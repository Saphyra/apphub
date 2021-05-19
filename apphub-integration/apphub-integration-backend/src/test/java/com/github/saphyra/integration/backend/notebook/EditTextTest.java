package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.EditTextRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.TextResponse;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.INVALID_PARAM;
import static org.assertj.core.api.Assertions.assertThat;

public class EditTextTest extends TestBase {
    private static final String ORIGINAL_TITLE = "original-title";
    private static final String ORIGINAL_CONTENT = "original-content";
    private static final String NEW_CONTENT = "new-content";
    private static final String NEW_TITLE = "new-title";

    @DataProvider(name = "localeDataProvider")
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void blankTitle(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest request = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content(ORIGINAL_CONTENT)
            .build();
        UUID textId = NotebookActions.createText(language, accessTokenId, request);

        EditTextRequest editTextRequest = EditTextRequest.builder()
            .title(" ")
            .content(NEW_CONTENT)
            .build();
        Response response = NotebookActions.getEditTextResponse(language, accessTokenId, textId, editTextRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("title")).isEqualTo("must not be null or blank");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test(dataProvider = "localeDataProvider")
    public void nullContent(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest request = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content(ORIGINAL_CONTENT)
            .build();
        UUID textId = NotebookActions.createText(language, accessTokenId, request);

        EditTextRequest editTextRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(null)
            .build();
        Response response = NotebookActions.getEditTextResponse(language, accessTokenId, textId, editTextRequest);

        assertThat(response.getStatusCode()).isEqualTo(400);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getParams().get("content")).isEqualTo("must not be null");
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, INVALID_PARAM));
    }

    @Test
    public void editText() {
        Language language = Language.HUNGARIAN;

        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateTextRequest request = CreateTextRequest.builder()
            .title(ORIGINAL_TITLE)
            .content(ORIGINAL_CONTENT)
            .build();
        UUID textId = NotebookActions.createText(language, accessTokenId, request);

        EditTextRequest editTextRequest = EditTextRequest.builder()
            .title(NEW_TITLE)
            .content(NEW_CONTENT)
            .build();
        NotebookActions.editText(language, accessTokenId, textId, editTextRequest);

        TextResponse textResponse = NotebookActions.getText(language, accessTokenId, textId);
        assertThat(textResponse.getTitle()).isEqualTo(NEW_TITLE);
        assertThat(textResponse.getContent()).isEqualTo(NEW_CONTENT);
    }
}
