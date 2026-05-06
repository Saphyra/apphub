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
        String accessToken = IndexPageActions.registerAndLogin(getServerPort(), userData);

        UUID listItemId = TextActions.createText(getServerPort(), accessToken, CreateTextRequest.builder().title(TITLE).content("").build());
        PinActions.pin(getServerPort(), accessToken, listItemId, true);

        create_blankName(accessToken);
        create_tooLongName(accessToken);
        UUID pinGroupId = create(accessToken);

        rename_blankName(accessToken, pinGroupId);
        rename_tooLongName(accessToken, pinGroupId);
        rename(accessToken, pinGroupId);

        addItem(accessToken, pinGroupId, listItemId);

        assertThat(PinActions.getPinnedItems(getServerPort(), accessToken, pinGroupId)).hasSize(1)
            .extracting(NotebookView::getId)
            .containsExactly(listItemId);

        removeItem(accessToken, pinGroupId, listItemId);

        delete(accessToken, pinGroupId);
    }

    private void delete(String accessToken, UUID pinGroupId) {
        assertThat(PinActions.deletePinGroup(getServerPort(), accessToken, pinGroupId)).isEmpty();
    }

    private void removeItem(String accessToken, UUID pinGroupId, UUID listItemId) {
        List<NotebookView> groupMembers = PinActions.removeItemFromPinGroup(getServerPort(), accessToken, pinGroupId, listItemId);

        assertThat(groupMembers).isEmpty();
    }

    private void addItem(String accessToken, UUID pinGroupId, UUID listItemId) {
        List<NotebookView> groupMembers = PinActions.addItemToPinGroup(getServerPort(), accessToken, pinGroupId, listItemId);

        assertThat(groupMembers).hasSize(1)
            .extracting(NotebookView::getId)
            .containsExactly(listItemId);
    }

    private void rename(String accessToken, UUID pinGroupId) {
        List<PinGroupResponse> pinGroups = PinActions.renamePinGroup(getServerPort(), accessToken, pinGroupId, NEW_PIN_GROUP_NAME);

        assertThat(pinGroups).hasSize(1);
        assertThat(pinGroups.get(0).getPinGroupName()).isEqualTo(NEW_PIN_GROUP_NAME);
    }

    private void rename_tooLongName(String accessToken, UUID pinGroupId) {
        Response response = PinActions.getRenamePinGroupResponse(getServerPort(), accessToken, pinGroupId, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "too long");
    }

    private void rename_blankName(String accessToken, UUID pinGroupId) {
        Response response = PinActions.getRenamePinGroupResponse(getServerPort(), accessToken, pinGroupId, " ");

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "must not be null or blank");
    }

    private UUID create(String accessToken) {
        List<PinGroupResponse> pinGroups = PinActions.createPinGroup(getServerPort(), accessToken, PIN_GROUP_NAME);

        assertThat(pinGroups).hasSize(1);
        assertThat(pinGroups.get(0).getPinGroupName()).isEqualTo(PIN_GROUP_NAME);

        return pinGroups.get(0)
            .getPinGroupId();
    }

    private void create_tooLongName(String accessToken) {
        Response response = PinActions.getCreatePinGroupResponse(getServerPort(), accessToken, Stream.generate(() -> "a").limit(31).collect(Collectors.joining()));

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "too long");
    }

    private void create_blankName(String accessToken) {
        Response response = PinActions.getCreatePinGroupResponse(getServerPort(), accessToken, " ");

        ResponseValidator.verifyInvalidParam(response, "pinGroupName", "must not be null or blank");
    }
}
