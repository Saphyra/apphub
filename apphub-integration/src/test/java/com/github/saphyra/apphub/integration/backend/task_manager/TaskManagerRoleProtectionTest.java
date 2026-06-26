package com.github.saphyra.apphub.integration.backend.task_manager;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerInvitationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerNotificationActions;
import com.github.saphyra.apphub.integration.action.backend.task_manager.TaskManagerOrganizationActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.CommonUtils;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.task_manager.notification.SetNotificationStatusRequest;
import com.github.saphyra.apphub.integration.structure.api.task_manager.organization.CreateOrganizationRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

public class TaskManagerRoleProtectionTest extends BackEndTest {
    @Test(dataProvider = "roleProvider", groups = {"be", "task-manager", "role-protection"})
    public void taskManagerRoleProtection(String role) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        DynamoDbUtil.removeRoleByEmail(userData.getEmail(), role);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        //Organization
        CommonUtils.verifyMissingRole(() -> TaskManagerOrganizationActions.createOrganizationResponse(getServerPort(), accessToken, new CreateOrganizationRequest()));
        CommonUtils.verifyMissingRole(() -> TaskManagerOrganizationActions.getOrganizationsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> TaskManagerOrganizationActions.getOrganizationResponse(getServerPort(), accessToken, UUID.randomUUID()));

        //Invitation
        CommonUtils.verifyMissingRole(() -> TaskManagerInvitationActions.getInvitationsResponse(getServerPort(), accessToken));
        CommonUtils.verifyMissingRole(() -> TaskManagerInvitationActions.acceptInvitationResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TaskManagerInvitationActions.rejectInvitationResponse(getServerPort(), accessToken, UUID.randomUUID()));

        //Notification
        CommonUtils.verifyMissingRole(() -> TaskManagerNotificationActions.getNotificationsResponse(getServerPort(), accessToken, UUID.randomUUID()));
        CommonUtils.verifyMissingRole(() -> TaskManagerNotificationActions.setNotificationStatusResponse(getServerPort(), accessToken, new SetNotificationStatusRequest()));
        CommonUtils.verifyMissingRole(() -> TaskManagerNotificationActions.deleteNotificationsResponse(getServerPort(), accessToken, List.of()));
    }

    @DataProvider(parallel = true)
    public Object[][] roleProvider() {
        return new Object[][]{
            new Object[]{Constants.ROLE_TASK_MANAGER},
            new Object[]{Constants.ROLE_ACCESS}
        };
    }
}
