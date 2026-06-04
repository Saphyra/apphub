package com.github.saphyra.apphub.integration.backend.notebook;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.CategoryActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.FileActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ImageActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.LinkActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ListItemActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.OnlyTitleActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.PinActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TableActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.TextActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateCategoryRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateFileRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateLinkRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateOnlyTitleRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTableRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.CreateTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditListItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.EditTextRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.AddChecklistItemRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.EditChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.notebook.table.EditTableRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.UUID;

public class NotebookRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "notebook", "role-protection"})
    public void notebookRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DynamoDbUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        //Text
        CommonUtils.verifyMissingRole(() -> TextActions.getCreateTextResponse(getServerPort(), accessToken, new CreateTextRequest()));
        CommonUtils.verifyMissingRole(() -> TextActions.getTextResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TextActions.getEditTextResponse(getServerPort(), accessToken, UUID.randomUUID(), new EditTextRequest()));

        //Table
        CommonUtils.verifyMissingRole(() -> TableActions.getCreateTableResponse(getServerPort(), accessToken, new CreateTableRequest()));
        CommonUtils.verifyMissingRole(() -> TableActions.getEditTableResponse(getServerPort(), accessToken, UUID.randomUUID(), new EditTableRequest()));
        CommonUtils.verifyMissingRole(() -> TableActions.getTableResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TableActions.getUpdateChecklistTableRowStatusResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> TableActions.getDeleteCheckedResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TableActions.getEditCheckboxStatusResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(), false));

        //Pin
        CommonUtils.verifyMissingRole(() -> PinActions.getPinResponse(getServerPort(), accessToken, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinnedItemsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> PinActions.getCreatePinGroupResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> PinActions.getRenamePinGroupResponse(getServerPort(), accessToken, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinGroupsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> PinActions.getDeletePinGroupResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getAddItemToPinGroupResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getReniveItemFromPinGroupResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinGroupOpenedResponse(getServerPort(), accessToken, UUID.randomUUID()));

        //Only title
        CommonUtils.verifyMissingRole(() -> OnlyTitleActions.getCreateOnlyTitleResponse(getServerPort(), accessToken, new CreateOnlyTitleRequest()));

        //List item
        CommonUtils.verifyMissingRole(() -> ListItemActions.getFindListItemResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getDeleteListItemResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getEditListItemResponse(getServerPort(), accessToken, new EditListItemRequest(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getMoveListItemResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getCloneListItemResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getSearchResponse(getServerPort(), accessToken, ""));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getArchiveResponse(getServerPort(), accessToken, UUID.randomUUID(), false));

        //Link
        CommonUtils.verifyMissingRole(() -> LinkActions.getCreateLinkResponse(getServerPort(), accessToken, new CreateLinkRequest()));

        //Image
        CommonUtils.verifyMissingRole(() -> ImageActions.getCreateImageResponse(getServerPort(), accessToken, new CreateFileRequest()));

        //File
        CommonUtils.verifyMissingRole(() -> FileActions.getCreateFileResponse(getServerPort(), accessToken, new CreateFileRequest()));

        //Checklist
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessToken, new CreateChecklistRequest()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getEditChecklistResponse(getServerPort(), accessToken, new EditChecklistRequest(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getChecklistResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getUpdateChecklistItemStatusResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getDeleteChecklistItemResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getDeleteCheckedChecklistItemsResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getOrderItemsResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getEditChecklistItemResponse(getServerPort(), accessToken, UUID.randomUUID(), UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getAddChecklistItemResponse(getServerPort(), accessToken, UUID.randomUUID(), new AddChecklistItemRequest()));

        //Category
        CommonUtils.verifyMissingRole(() -> CategoryActions.getCreateCategoryResponse(getServerPort(), accessToken, new CreateCategoryRequest()));
        CommonUtils.verifyMissingRole(() -> CategoryActions.getCategoryTreeResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> CategoryActions.getChildrenOfCategoryResponse(getServerPort(), accessToken, UUID.randomUUID(), Collections.emptyList()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_NOTEBOOK},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
