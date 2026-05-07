package com.github.saphyra.apphub.integration.backend.notebook.pin;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID listItemId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title(TITLE).content("").build());

        pin_nullPinned(accessToken, listItemId);
        pin(accessToken, listItemId);
        unpin(accessToken, listItemId);
    }

    private static void pin_nullPinned(String accessToken, UUID listItemId) {
        Response pin_nullPinnedResponse = PinActions.getPinResponse(getServerPort(), accessToken, listItemId, null);
        ResponseValidator.verifyInvalidParam(pin_nullPinnedResponse, "pinned", "must not be null");
    }

    private static void pin(String accessToken, UUID listItemId) {
        PinActions.pin(getServerPort(), accessToken, listItemId, true);

        List<NotebookView> pinnedItems = PinActions.getPinnedItems(getServerPort(), accessToken);
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
    }

    private static void unpin(String accessToken, UUID listItemId) {
        List<NotebookView> pinnedItems;
        PinActions.pin(getServerPort(), accessToken, listItemId, false);

        pinnedItems = PinActions.getPinnedItems(getServerPort(), accessToken);
        assertThat(pinnedItems).isEmpty();
    }
}
