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
        UUID accessTokenId = IndexPageActions.registerAndLogin(userData);

        DatabaseUtil.removeRoleByEmail(userData.getEmail(), role);

        SleepUtil.sleep(3000);

        //Text
        CommonUtils.verifyMissingRole(() -> TextActions.getCreateTextResponse(accessTokenId, new CreateTextRequest()));
        CommonUtils.verifyMissingRole(() -> TextActions.getTextResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TextActions.getEditTextResponse(accessTokenId, UUID.randomUUID(), new EditTextRequest()));

        //Table
        CommonUtils.verifyMissingRole(() -> TableActions.getCreateTableResponse(accessTokenId, new CreateTableRequest()));
        CommonUtils.verifyMissingRole(() -> TableActions.getEditTableResponse(accessTokenId, UUID.randomUUID(), new EditTableRequest()));
        CommonUtils.verifyMissingRole(() -> TableActions.getTableResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TableActions.getUpdateChecklistTableRowStatusResponse(accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> TableActions.getDeleteCheckedResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TableActions.getEditCheckboxStatusResponse(accessTokenId, UUID.randomUUID(), false));

        //Pin
        CommonUtils.verifyMissingRole(() -> PinActions.getPinResponse(accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinnedItemsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> PinActions.getCreatePinGroupResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> PinActions.getRenamePinGroupResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinGroupsResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> PinActions.getDeletePinGroupResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getAddItemToPinGroupResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getReniveItemFromPinGroupResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> PinActions.getPinGroupOpenedResponse(accessTokenId, UUID.randomUUID()));

        //Only title
        CommonUtils.verifyMissingRole(() -> OnlyTitleActions.getCreateOnlyTitleResponse(accessTokenId, new CreateOnlyTitleRequest()));

        //List item
        CommonUtils.verifyMissingRole(() -> ListItemActions.getFindListItemResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getDeleteListItemResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getEditListItemResponse(accessTokenId, new EditListItemRequest(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getMoveListItemResponse(accessTokenId, UUID.randomUUID(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getCloneListItemResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getSearchResponse(accessTokenId, ""));
        CommonUtils.verifyMissingRole(() -> ListItemActions.getArchiveResponse(accessTokenId, UUID.randomUUID(), false));

        //Link
        CommonUtils.verifyMissingRole(() -> LinkActions.getCreateLinkResponse(accessTokenId, new CreateLinkRequest()));

        //Image
        CommonUtils.verifyMissingRole(() -> ImageActions.getCreateImageResponse(accessTokenId, new CreateFileRequest()));

        //File
        CommonUtils.verifyMissingRole(() -> FileActions.getCreateFileResponse(accessTokenId, new CreateFileRequest()));

        //Checklist
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getCreateChecklistItemResponse(accessTokenId, new CreateChecklistRequest()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getEditChecklistResponse(accessTokenId, new EditChecklistRequest(), UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getChecklistResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getUpdateChecklistItemStatusResponse(accessTokenId, UUID.randomUUID(), false));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getDeleteChecklistItemResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getDeleteCheckedChecklistItemsResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getOrderItemsResponse(accessTokenId, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getEditChecklistItemResponse(accessTokenId, UUID.randomUUID(), ""));
        CommonUtils.verifyMissingRole(() -> ChecklistActions.getAddChecklistItemResponse(accessTokenId, UUID.randomUUID(), new AddChecklistItemRequest()));

        //Category
        CommonUtils.verifyMissingRole(() -> CategoryActions.getCreateCategoryResponse(accessTokenId, new CreateCategoryRequest()));
        CommonUtils.verifyMissingRole(() -> CategoryActions.getCategoryTreeResponse(accessTokenId));
        CommonUtils.verifyMissingRole(() -> CategoryActions.getChildrenOfCategoryResponse(accessTokenId, UUID.randomUUID(), Collections.emptyList()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_NOTEBOOK},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
