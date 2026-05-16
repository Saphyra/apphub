package com.github.saphyra.apphub.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.backend.AccessTokenActions;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DynamoDbUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
public class BanExpirationTest extends BackEndTest {
    private static final String REASON = "reason";

    @Test(groups = {"be", "admin-panel"})
    public void userCanAccessApplicationWhenBanExpired() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        int serverPort = getServerPort();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        DynamoDbUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        tokenResponse = AccessTokenActions.refresh(getServerPort(), tokenResponse.getRefreshToken().getJwt());
        String adminAccessToken = tokenResponse.getAccessToken()
            .getJwt();

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(serverPort, testUser.toRegistrationRequest());
        UUID testUserId = DynamoDbUtil.getUserIdByEmail(testUser.getEmail());

        //Ban
        BanRequest banRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_ACCESS)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        BanActions.ban(serverPort, adminAccessToken, banRequest);

        String testAccessToken = IndexPageActions.login(serverPort, testUser.toLoginRequest())
            .getAccessToken()
            .getJwt();

        Response response = ModulesActions.getModulesResponse(serverPort, testAccessToken);
        ResponseValidator.verifyErrorResponse(response, 403, ErrorCode.MISSING_ROLE);

        AwaitilityWrapper.create(300, 10)
            .until(() -> {
                log.debug("Checking if user is unlocked...");
                String token = IndexPageActions.login(serverPort, testUser.toLoginRequest())
                    .getAccessToken()
                    .getJwt();

                return ModulesActions.getModulesResponse(serverPort, token).getStatusCode() == 200;
            })
            .assertTrue("Ban was not revoked in the given time.");
    }
}
