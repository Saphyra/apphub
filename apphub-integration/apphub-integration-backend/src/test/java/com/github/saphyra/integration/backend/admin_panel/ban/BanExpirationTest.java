package com.github.saphyra.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.ResponseValidator;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.ModulesActions;
import com.github.saphyra.apphub.integration.backend.actions.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.backend.model.account.BanRequest;
import com.github.saphyra.apphub.integration.common.framework.AwaitilityWrapper;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Slf4j
public class BanExpirationTest extends BackEndTest {
    private static final String REASON = "reason";

    @Test
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
                log.info("Checking if user is unlocked...");
                return ModulesActions.getModulesResponse(language, testAccessTokenId).getStatusCode() == 200;
            })
            .assertTrue("Ban was not revoked in the given time.");
    }
}
