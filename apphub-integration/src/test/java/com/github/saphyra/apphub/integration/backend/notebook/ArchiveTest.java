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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID listItemId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title(TITLE).content("").build());

        archive_nullArchived(accessToken, listItemId);
        archive(accessToken, listItemId);
        unarchive(accessToken, listItemId);
    }

    private static void archive_nullArchived(String accessToken, UUID listItemId) {
        Response pin_nullPinnedResponse = ListItemActions.getArchiveResponse(getServerPort(), accessToken, listItemId, null);
        ResponseValidator.verifyInvalidParam(pin_nullPinnedResponse, "archived", "must not be null");
    }

    private static void archive(String accessToken, UUID listItemId) {
        ListItemActions.archive(getServerPort(), accessToken, listItemId, true);

        List<NotebookView> pinnedItems = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren();
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
        assertThat(pinnedItems.get(0).isArchived()).isTrue();
    }

    private static void unarchive(String accessToken, UUID listItemId) {
        List<NotebookView> pinnedItems;
        ListItemActions.archive(getServerPort(), accessToken, listItemId, false);

        pinnedItems = CategoryActions.getChildrenOfCategory(getServerPort(), accessToken, null)
            .getChildren();
        assertThat(pinnedItems).hasSize(1);
        assertThat(pinnedItems.get(0).getId()).isEqualTo(listItemId);
        assertThat(pinnedItems.get(0).isArchived()).isFalse();
    }
}
