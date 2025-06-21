package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.action.backend.notebook.ChecklistActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.structure.api.notebook.ItemType;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.ChecklistItemModel;
import com.github.saphyra.apphub.integration.structure.api.notebook.checklist.CreateChecklistRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class RowSwapProtectionTest extends BackEndTest {
    private static final String CHECKLIST_TITLE = "checklist-title";

    @Test(groups = {"be", "misc"})
    public void rowSwapProtectionTest() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);

        ModulesActions.setAsFavorite(getServerPort(), accessTokenId, "account", true);
        CreateChecklistRequest createChecklistRequest = CreateChecklistRequest.builder()
            .title(CHECKLIST_TITLE)
            .items(List.of(
                ChecklistItemModel.builder()
                    .index(0)
                    .checked(true)
                    .content("")
                    .type(ItemType.NEW)
                    .build()
            ))
            .build();
        ChecklistActions.createChecklist(getServerPort(), accessTokenId, createChecklistRequest);

        UUID userId = DatabaseUtil.getUserIdByEmail(userData.getEmail());
        String data = DatabaseUtil.getEncryptedDataFromCheckedItem(userId);
        DatabaseUtil.injectEncryptedDataToModules(userId, data);

        assertThat(ModulesActions.getModulesResponse(getServerPort(), accessTokenId).getStatusCode()).isNotEqualTo(200);
    }
}
