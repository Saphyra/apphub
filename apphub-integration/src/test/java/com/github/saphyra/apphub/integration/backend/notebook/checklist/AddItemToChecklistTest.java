package com.github.saphyra.apphub.integration.backend.notebook.checklist;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class AddItemToChecklistTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String CONTENT = "content";
    private static final String NEW_CONTENT = "new-content";

    @Test(groups = {"be", "notebook"})
    public void addItemToChecklist() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        CreateChecklistRequest createRequest = CreateChecklistRequest.builder()
            .title(TITLE)
            .items(Arrays.asList(ChecklistItemModel.builder()
                .index(0)
                .checked(true)
                .content(CONTENT)
                .build()))
            .build();
        UUID listItemId = ChecklistActions.createChecklist(getServerPort(), accessTokenId, createRequest);

        nullContent(accessTokenId, listItemId);
        nullIndex(accessTokenId, listItemId);
        addItemToTheStart(accessTokenId, listItemId);
    }

    private void nullContent(UUID accessTokenId, UUID listItemId) {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .content(null)
            .index(-1)
            .build();

        Response response = ChecklistActions.getAddChecklistItemResponse(getServerPort(), accessTokenId, listItemId, request);

        ResponseValidator.verifyInvalidParam(response, "content", "must not be null");
    }

    private void nullIndex(UUID accessTokenId, UUID listItemId) {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .content(NEW_CONTENT)
            .index(null)
            .build();

        Response response = ChecklistActions.getAddChecklistItemResponse(getServerPort(), accessTokenId, listItemId, request);

        ResponseValidator.verifyInvalidParam(response, "index", "must not be null");
    }

    private void addItemToTheStart(UUID accessTokenId, UUID listItemId) {
        AddChecklistItemRequest request = AddChecklistItemRequest.builder()
            .content(NEW_CONTENT)
            .index(-1)
            .build();

        ChecklistActions.addChecklistItem(getServerPort(), accessTokenId, listItemId, request);

        ChecklistResponse checklistResponse = ChecklistActions.getChecklist(getServerPort(), accessTokenId, listItemId);

        assertThat(checklistResponse.getItems()).hasSize(2);
        List<ChecklistItemModel> items = checklistResponse.getItems()
            .stream()
            .sorted(Comparator.comparing(ChecklistItemModel::getIndex))
            .toList();

        assertThat(items.get(0).getContent()).isEqualTo(NEW_CONTENT);
        assertThat(items.get(0).getIndex()).isEqualTo(-1);
        assertThat(items.get(0).getChecked()).isFalse();

        assertThat(items.get(1).getContent()).isEqualTo(CONTENT);
        assertThat(items.get(1).getIndex()).isEqualTo(0);
        assertThat(items.get(1).getChecked()).isTrue();
    }
}
