package com.github.saphyra.apphub.integraton.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.ModulesActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
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

    @Test(priority = -1)
    public void userCanAccessApplicationWhenBanExpired() {
        Language language = Language.HUNGARIAN;
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        UUID testAccessTokenId = IndexPageActions.registerAndLogin(language, testUser);
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

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

        BanActions.ban(language, accessTokenId, banRequest);

        Response response = ModulesActions.getModulesResponse(language, testAccessTokenId);
        ResponseValidator.verifyErrorResponse(language, response, 403, ErrorCode.MISSING_ROLE);

        AwaitilityWrapper.create(300, 10)
            .until(() -> {
                log.debug("Checking if user is unlocked...");
                return ModulesActions.getModulesResponse(language, testAccessTokenId).getStatusCode() == 200;
            })
            .assertTrue("Ban was not revoked in the given time.");
    }
}
