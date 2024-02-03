package com.github.saphyra.apphub.integration.backend.notebook.pin;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.NotebookView;
import com.github.saphyra.apphub.integration.structure.api.notebook.PinGroupResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class PinGroupTest extends BackEndTest {
    private static final String TITLE = "title";
    private static final String PIN_GROUP_NAME = "pin-group-name";
    private static final String NEW_PIN_GROUP_NAME = "new-pin-group-name";

    @Test(groups = {"be", "notebook"})
    public void pinListItem() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        UUID listItemId = TextActions.createText(accessTokenId, CreateTextRequest.builder().title(TITLE).content("").build());
        PinActions.pin(accessTokenId, listItemId, true);

        create_blankName(accessTokenId);
        create_tooLongName(accessTokenId);
        UUID pinGroupId = create(accessTokenId);

        rename_blankName(accessTokenId, pinGroupId);
        rename_tooLongName(accessTokenId, pinGroupId);
        rename(accessTokenId, pinGroupId);

        addItem(accessTokenId, pinGroupId, listItemId);

        assertThat(PinActions.getPinnedItems(accessTokenId, pinGroupId)).hasSize(1)
            .extracting(NotebookView::getId)
            .containsExactly(listItemId);

        removeItem(accessTokenId, pinGroupId, listItemId);

        delete(accessTokenId, pinGroupId);
    }

    private void delete(UUID accessTokenId, UUID pinGroupId) {
        assertThat(PinActions.deletePinGroup(accessTokenId, pinGroupId)).isEmpty();
    }

    private void removeItem(UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        List<NotebookView> groupMembers = PinActions.removeItemFromPinGroup(accessTokenId, pinGroupId, listItemId);

        assertThat(groupMembers).isEmpty();
    }

    private void addItem(UUID accessTokenId, UUID pinGroupId, UUID listItemId) {
        List<NotebookView> groupMembers = PinActions.addItemToPinGroup(accessTokenId, pinGroupId, listItemId);

        assertThat(groupMembers).hasSize(1)
            .extracting(NotebookView::getId)
            .containsExactly(listItemId);
    }

    private void rename(UUID accessTokenId, UUID pinGroupId) {
        List<PinGroupResponse> pinGroups = PinActions.renamePinGroup(accessTokenId, pinGroupId, NEW_PIN_GROUP_NAME);

        assertThat(pinGroups).hasSize(1);
        assertThat(pinGroups.get(0).getPinGroupName()).isEqualTo(NEW_PIN_GROUP_NAME);
    }

    private void rename_tooLongName(UUID accessTokenId, UUID pinGroupId) {
        Response response = PinActions.getRenamePinGroupResponse(accessTokenId, pinGroupId, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "too long");
    }

    private void rename_blankName(UUID accessTokenId, UUID pinGroupId) {
        Response response = PinActions.getRenamePinGroupResponse(accessTokenId, pinGroupId, " ");

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "must not be null or blank");
    }

    private UUID create(UUID accessTokenId) {
        List<PinGroupResponse> pinGroups = PinActions.createPinGroup(accessTokenId, PIN_GROUP_NAME);

        assertThat(pinGroups).hasSize(1);
        assertThat(pinGroups.get(0).getPinGroupName()).isEqualTo(PIN_GROUP_NAME);

        return pinGroups.get(0)
            .getPinGroupId();
    }

    private void create_tooLongName(UUID accessTokenId) {
        Response response = PinActions.getCreatePinGroupResponse(accessTokenId, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "too long");
    }

    private void create_blankName(UUID accessTokenId) {
        Response response = PinActions.getCreatePinGroupResponse(accessTokenId, " ");

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "must not be null or blank");
    }
}
