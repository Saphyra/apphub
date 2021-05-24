package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey.LIST_ITEM_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;

public class GetChecklistTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final Integer ORDER = 23;

    @Test(dataProvider = "localeDataProvider")
    public void listItemNotFound(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        Response response = NotebookActions.getChecklistResponse(language, accessTokenId, UUID.randomUUID());

        assertThat(response.getStatusCode()).isEqualTo(404);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.LIST_ITEM_NOT_FOUND.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LIST_ITEM_NOT_FOUND));
    }

    @Test
    public void getChecklist() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest checklistRequest = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(ChecklistItemNodeRequest.builder()
                .content(CONTENT)
                .checked(true)
                .order(ORDER)
                .build()))
            .build();
        UUID checklistId = NotebookActions.createChecklist(language, accessTokenId, checklistRequest);

        ChecklistResponse response = NotebookActions.getChecklist(language, accessTokenId, checklistId);

        assertThat(response.getTitle()).isEqualTo(TITLE);
        assertThat(response.getNodes()).hasSize(1);
        assertThat(response.getNodes().get(0).getChecklistItemId()).isNotNull();
        assertThat(response.getNodes().get(0).getContent()).isEqualTo(CONTENT);
        assertThat(response.getNodes().get(0).getChecked()).isTrue();
        assertThat(response.getNodes().get(0).getOrder()).isEqualTo(ORDER);
    }
}
