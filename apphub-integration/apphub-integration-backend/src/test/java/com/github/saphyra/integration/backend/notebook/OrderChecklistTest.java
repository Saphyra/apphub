package com.github.saphyra.integration.backend.notebook;

import com.github.saphyra.apphub.integration.backend.actions.NotebookActions;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemNodeRequest;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistItemResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.ChecklistResponse;
import com.github.saphyra.apphub.integration.backend.model.notebook.CreateChecklistItemRequest;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class OrderChecklistTest extends TestBase {
    private static final String TITLE = "title";

    @Test
    public void orderItems() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        CreateChecklistItemRequest request = CreateChecklistItemRequest.builder()
            .title(TITLE)
            .nodes(Arrays.asList(
                ChecklistItemNodeRequest.builder()
                    .order(1)
                    .checked(true)
                    .content("A")
                    .build(),
                ChecklistItemNodeRequest.builder()
                    .order(0)
                    .checked(true)
                    .content("B")
                    .build()
            ))
            .build();

        UUID listItemId = NotebookActions.createChecklist(language, accessTokenId, request);

        Response response = NotebookActions.orderChecklistItems(language, accessTokenId, listItemId);

        assertThat(response.getStatusCode()).isEqualTo(200);

        ChecklistResponse checklistResponse = NotebookActions.getChecklist(language, accessTokenId, listItemId);

        assertThat(checklistResponse.getNodes()).hasSize(2);
        assertThat(findByOrder(checklistResponse.getNodes(), 0).getContent()).isEqualTo("A");
        assertThat(findByOrder(checklistResponse.getNodes(), 1).getContent()).isEqualTo("B");
    }

    private ChecklistItemResponse findByOrder(List<ChecklistItemResponse> nodes, int order) {
        return nodes.stream()
            .filter(checklistItemResponse -> checklistItemResponse.getOrder().equals(order))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No checklistItem found with order " + 2));
    }
}
