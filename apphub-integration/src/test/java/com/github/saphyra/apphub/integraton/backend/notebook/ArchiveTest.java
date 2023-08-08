package com.github.saphyra.apphub.integraton.backend.notebook;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.NotebookActions;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ArchiveTest extends BackEndTest {
    private static final String TITLE = "title";

    @Test(dataProvider = "languageDataProvider")
    public void archiveListItem(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        UUID listItemId = NotebookActions.createText(language, accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());

        //Archive - Null archived
        Response pin_nullPinnedResponse = NotebookActions.getArchiveResponse(language, accessTokenId, listItemId, null);
        ResponseValidator.verifyInvalidParam(language, pin_nullPinnedResponse, "archived", "must not be null");

        //Archive
        NotebookActions.archive(language, accessTokenId, listItemId, true);

        List<NotebookView> pinnedItems = NotebookActions.getChildrenOfCategory(language, accessTokenId, null)
            .getChildren();
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
        assertThat(pinnedItems.get(0).isArchived()).isTrue();

        //Unarchive
        NotebookActions.archive(language, accessTokenId, listItemId, false);

        pinnedItems = NotebookActions.getChildrenOfCategory(language, accessTokenId, null)
            .getChildren();
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
        assertThat(pinnedItems.get(0).isArchived()).isFalse();
    }
}
