package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
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

public class ArchiveTest extends BackEndTest {
    private static final String TITLE = "title";

    @Test(groups = {"be", "notebook"})
    public void archiveListItem() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        UUID listItemId = TextActions.createText(accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());

        archive_nullArchived(accessTokenId, listItemId);
        archive(accessTokenId, listItemId);
        unarchive(accessTokenId, listItemId);
    }

    private static void archive_nullArchived(UUID accessTokenId, UUID listItemId) {
        Response pin_nullPinnedResponse = ListItemActions.getArchiveResponse(accessTokenId, listItemId, null);
        ResponseValidator.verifyInvalidParam(pin_nullPinnedResponse, "archived", "must not be null");
    }

    private static void archive(UUID accessTokenId, UUID listItemId) {
        ListItemActions.archive(accessTokenId, listItemId, true);

        List<NotebookView> pinnedItems = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren();
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
        assertThat(pinnedItems.get(0).isArchived()).isTrue();
    }

    private static void unarchive(UUID accessTokenId, UUID listItemId) {
        List<NotebookView> pinnedItems;
        ListItemActions.archive(accessTokenId, listItemId, false);

        pinnedItems = CategoryActions.getChildrenOfCategory(accessTokenId, null)
            .getChildren();
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
        assertThat(pinnedItems.get(0).isArchived()).isFalse();
    }
}
