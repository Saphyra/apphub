package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class UpdateChecklistItemStatusTest extends TestBase {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";

    @DataProvider(name = "localeDataProvider", parallel = true)
    public Object[] localeDataProvider() {
        return Language.values();
    }

    @Test(dataProvider = "localeDataProvider")
    public void checklistItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getUpdateChecklistItemStatusResponse(language, accessTokenId, UUID.randomUUID(), true);

        assertThat(response.getStatusCode()).isEqualTo(404);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.ERROR_CODE_LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void check() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(0)
                .content(CONTENT)
                .checked(false)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistItem(language, accessTokenId, createChecklistItemRequest);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        NotebookActions.updateChecklistItemStatus(language, accessTokenId, checklistResponse.getNodes().get(0).getChecklistItemId(), true);

        assertThat(NotebookActions.getChecklist(language, accessTokenId, listItemId).getNodes().get(0).getChecked()).isTrue();
    }


    @Test
    public void uncheck() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest createChecklistItemRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .order(0)
                .content(CONTENT)
                .checked(true)
                .build()))
            .build();

        UUID listItemId = NotebookActions.createChecklistItem(language, accessTokenId, createChecklistItemRequest);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        NotebookActions.updateChecklistItemStatus(language, accessTokenId, checklistResponse.getNodes().get(0).getChecklistItemId(), false);

        assertThat(NotebookActions.getChecklist(language, accessTokenId, listItemId).getNodes().get(0).getChecked()).isFalse();
    }
}
