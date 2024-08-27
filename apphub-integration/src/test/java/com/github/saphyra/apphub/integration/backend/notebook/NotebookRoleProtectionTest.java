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
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.SleepUtil;
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
    @Test(dataProvider = "roleProvider", groups = {"be", "notebook"})
    public void notebookRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Text
        CommonUtils.verifyMissingRole(() -> TextActions.getCreateTextResponse(getServerPort(), accessTokenId, new CreateTextRequest()));
        CommonUtils.verifyMissingRole(() -> TextActions.getTextResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TextActions.getEditTextResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new EditTextRequest()));

        //Table
        CommonUtils.verifyMissingRole(() -> TableActions.getCreateTableResponse(getServerPort(), accessTokenId, new CreateTableRequest()));
        CommonUtils.verifyMissingRole(() -> TableActions.getEditTableResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new EditTableRequest()));
        CommonUtils.verifyMissingRole(() -> TableActions.getTableResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TableActions.getUpdateChecklistTableRowStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> TableActions.getDeleteCheckedResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TableActions.getEditCheckboxStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));

        //Pin
        CommonUtils.verifyMissingRole(() -> PinActions.getPinResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinnedItemsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> PinActions.getCreatePinGroupResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> PinActions.getRenamePinGroupResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinGroupsResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> PinActions.getDeletePinGroupResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getAddItemToPinGroupResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getReniveItemFromPinGroupResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinGroupOpenedResponse(getServerPort(), accessTokenId, UUID.randomUUID()));

        //Only title
        CommonUtils.verifyMissingRole(() -> OnlyTitleActions.getCreateOnlyTitleResponse(getServerPort(), accessTokenId, new CreateOnlyTitleRequest()));

        //List item
        CommonUtils.verifyMissingRole(() -> ListItemActions.getFindListItemResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getDeleteListItemResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getEditListItemResponse(getServerPort(), accessTokenId, new EditListItemRequest(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getMoveListItemResponse(getServerPort(), accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getCloneListItemResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getSearchResponse(getServerPort(), accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getArchiveResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));

        //Link
        CommonUtils.verifyMissingRole(() -> LinkActions.getCreateLinkResponse(getServerPort(), accessTokenId, new CreateLinkRequest()));

        //Image
        CommonUtils.verifyMissingRole(() -> ImageActions.getCreateImageResponse(getServerPort(), accessTokenId, new CreateFileRequest()));

        //File
        CommonUtils.verifyMissingRole(() -> FileActions.getCreateFileResponse(getServerPort(), accessTokenId, new CreateFileRequest()));

        //Checklist
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getCreateChecklistItemResponse(getServerPort(), accessTokenId, new CreateChecklistRequest()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getEditChecklistResponse(getServerPort(), accessTokenId, new EditChecklistRequest(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getChecklistResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getUpdateChecklistItemStatusResponse(getServerPort(), accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getDeleteChecklistItemResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getDeleteCheckedChecklistItemsResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getOrderItemsResponse(getServerPort(), accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getEditChecklistItemResponse(getServerPort(), accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getAddChecklistItemResponse(getServerPort(), accessTokenId, UUID.randomUUID(), new AddChecklistItemRequest()));

        //Category
        CommonUtils.verifyMissingRole(() -> CategoryActions.getCreateCategoryResponse(getServerPort(), accessTokenId, new CreateCategoryRequest()));
        CommonUtils.verifyMissingRole(() -> CategoryActions.getCategoryTreeResponse(getServerPort(), accessTokenId));
        CommonUtils.verifyMissingRole(() -> CategoryActions.getChildrenOfCategoryResponse(getServerPort(), accessTokenId, UUID.randomUUID(), Collections.emptyList()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_NOTEBOOK},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
