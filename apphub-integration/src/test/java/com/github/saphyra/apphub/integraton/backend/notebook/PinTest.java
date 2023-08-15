package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PinTest extends BackEndTest {
    private static final String TITLE = "title";

    @Test(groups = {"be", "notebook"})
    public void pinListItem() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        UUID listItemId = NotebookActions.createText(accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());

        pin_nullPinned(accessTokenId, listItemId);
        pin(accessTokenId, listItemId);
        unpin(accessTokenId, listItemId);
    }

    private static void pin_nullPinned(UUID accessTokenId, UUID listItemId) {
        Response pin_nullPinnedResponse = NotebookActions.getPinResponse(accessTokenId, listItemId, null);
        ResponseValidator.verifyInvalidParam(pin_nullPinnedResponse, "pinned", "must not be null");
    }

    private static void pin(UUID accessTokenId, UUID listItemId) {
        NotebookActions.pin(accessTokenId, listItemId, true);

        List<NotebookView> pinnedItems = NotebookActions.getPinnedItems(accessTokenId);
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
    }

    private static void unpin(UUID accessTokenId, UUID listItemId) {
        List<NotebookView> pinnedItems;
        NotebookActions.pin(accessTokenId, listItemId, false);

        pinnedItems = NotebookActions.getPinnedItems(accessTokenId);
        assertThat(pinnedItems).isEmpty();
    }
}
