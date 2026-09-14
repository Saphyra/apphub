package com.github.saphyra.apphub.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.backend.AccountActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.db.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.db.dynamodb.UserDynamoDbRepository;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import org.testng.annotations.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BanDataDeletedWithAccountTest extends BackEndTest {
    private static final String REASON = "reason";

    @Test(groups = {"be", "admin-panel"})
    public void banDataDeletedWithAccount() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerAndLogin(getServerPort(), userData);
        UUID userId = UserDynamoDbRepository.getUserIdByEmail(userData.getEmail());

        RegistrationParameters adminUserData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), adminUserData.toRegistrationRequest());
        UserDynamoDbRepository.addRoleByEmail(adminUserData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), adminUserData.toLoginRequest());
        String adminAccessToken = tokenResponse.getAccessToken()
            .getJwt();

        BanRequest banRequest = BanRequest.builder()
            .bannedUserId(userId)
            .bannedRole(Constants.ROLE_TRAINING)
            .permanent(true)
            .reason(REASON)
            .password(adminUserData.getPassword())
            .build();
        BanActions.ban(getServerPort(), adminAccessToken, banRequest);

        String accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        AccountActions.deleteAccount(getServerPort(), accessToken, userData.getPassword());

        AwaitilityWrapper.awaitAssert(() -> assertThat(DatabaseUtil.getRowCountByValue(userId, "apphub_user", "ban", "user_id")).isZero());
    }
}
