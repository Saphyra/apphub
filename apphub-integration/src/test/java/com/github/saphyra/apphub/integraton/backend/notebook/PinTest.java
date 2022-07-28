package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class PinTest extends BackEndTest {
    private static final String TITLE = "title";

    @Test(dataProvider = "languageDataProvider")
    public void pinListItem(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID listItemId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());

        //Pin - Null pinned
        Response pin_nullPinnedResponse = NotebookActions.getPinResponse(language, accessTokenId, listItemId, null);
        ResponseValidator.verifyInvalidParam(language, pin_nullPinnedResponse, "pinned", "must not be null");

        //Pin
        NotebookActions.pin(language, accessTokenId, listItemId, true);

        List<NotebookView> pinnedItems = NotebookActions.getPinnedItems(language, accessTokenId);
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);

        //Unpin
        NotebookActions.pin(language, accessTokenId, listItemId, false);

        pinnedItems = NotebookActions.getPinnedItems(language, accessTokenId);
        assertThat(pinnedItems).isEmpty();
    }
}
