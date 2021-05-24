package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class DeleteCheckedChecklistItemsTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String CHECKED_CONTENT = "checked-content";
    private static final String UNCHECKED_CONTENT = "unchecked-content";

    @Test
    public void deleteCheckedChecklistItems() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(
                ChecklistItemNodeRequest.builder()
                    .order(3412)
                    .checked(true)
                    .content(CHECKED_CONTENT)
                    .build(),
                ChecklistItemNodeRequest.builder()
                    .order(324)
                    .checked(false)
                    .content(UNCHECKED_CONTENT)
                    .build()
            ))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        Response response = NotebookActions.getDeleteCheckedChecklistItemsResponse(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        assertThat(checklistResponse.getNodes()).hasSize(1);
        assertThat(checklistResponse.getNodes().get(0).getContent()).isEqualTo(UNCHECKED_CONTENT);
    }
}
