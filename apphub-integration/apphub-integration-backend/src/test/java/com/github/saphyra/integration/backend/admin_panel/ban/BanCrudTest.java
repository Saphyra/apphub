package com.github.saphyra.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.ResponseValidator;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.backend.model.account.BanDetailsResponse;
import com.github.saphyra.apphub.integration.backend.model.account.BanRequest;
import com.github.saphyra.apphub.integration.backend.model.account.BanResponse;
import com.github.saphyra.apphub.integration.common.framework.Constants;
import com.github.saphyra.apphub.integration.common.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class BanCrudTest extends BackEndTest {
    private static final String REASON = "reason";

    @Test(dataProvider = "languageDataProvider")
    public void banCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        UUID adminUserId = DatabaseUtil.getUserIdByEmail(userData.getEmail());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        //Ban - Null bannedUserId
        BanRequest ban_nullBannedUserIdRequest = BanRequest.builder()
            .bannedUserId(null)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_nullBannedUserIdResponse = BanActions.getBanResponse(language, accessTokenId, ban_nullBannedUserIdRequest);

        ResponseValidator.verifyInvalidParam(language, ban_nullBannedUserIdResponse, "bannedUserId", "must not be null");

        //Ban - Blank bannedRole
        BanRequest ban_blankBannedRoleRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(" ")
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_blankBannedRoleResponse = BanActions.getBanResponse(language, accessTokenId, ban_blankBannedRoleRequest);

        ResponseValidator.verifyInvalidParam(language, ban_blankBannedRoleResponse, "bannedRole", "must not be null or blank");

        //Ban - Null permanent
        BanRequest ban_nullPermanentRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(null)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_nullPermanentResponse = BanActions.getBanResponse(language, accessTokenId, ban_nullPermanentRequest);

        ResponseValidator.verifyInvalidParam(language, ban_nullPermanentResponse, "permanent", "must not be null");

        //Ban - Blank reason
        BanRequest ban_blankReasonRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(" ")
            .password(userData.getPassword())
            .build();

        Response ban_blankReasonResponse = BanActions.getBanResponse(language, accessTokenId, ban_blankReasonRequest);

        ResponseValidator.verifyInvalidParam(language, ban_blankReasonResponse, "reason", "must not be null or blank");

        //Ban - Blank password
        BanRequest ban_blankPasswordRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(" ")
            .build();

        Response ban_blankPasswordResponse = BanActions.getBanResponse(language, accessTokenId, ban_blankPasswordRequest);

        ResponseValidator.verifyInvalidParam(language, ban_blankPasswordResponse, "password", "must not be null or blank");

        //Ban - Null duration
        BanRequest ban_nullDurationRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(null)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_nullDurationResponse = BanActions.getBanResponse(language, accessTokenId, ban_nullDurationRequest);

        ResponseValidator.verifyInvalidParam(language, ban_nullDurationResponse, "duration", "must not be null");

        //Ban - Duration too low
        BanRequest ban_durationTooLowRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(0)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_durationTooLowResponse = BanActions.getBanResponse(language, accessTokenId, ban_durationTooLowRequest);

        ResponseValidator.verifyInvalidParam(language, ban_durationTooLowResponse, "duration", "too low");

        //Ban - Unknown chronoUnit
        BanRequest ban_unknownChronoUnitRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit("asd")
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_unknownChronoUnitResponse = BanActions.getBanResponse(language, accessTokenId, ban_unknownChronoUnitRequest);

        ResponseValidator.verifyInvalidParam(language, ban_unknownChronoUnitResponse, "chronoUnit", "invalid value");

        //Ban - Incorrect password
        BanRequest ban_incorrectPasswordRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password("asd")
            .build();

        Response ban_incorrectPasswordResponse = BanActions.getBanResponse(language, accessTokenId, ban_incorrectPasswordRequest);

        ResponseValidator.verifyBadRequest(language, ban_incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);

        //Ban
        BanRequest banRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        BanActions.ban(language, accessTokenId, banRequest);

        //Get bans
        BanResponse bans = BanActions.getBans(language, accessTokenId, testUserId);

        assertThat(bans.getUserId()).isEqualTo(testUserId);
        assertThat(bans.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(bans.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(bans.getBans()).hasSize(1);
        BanDetailsResponse ban = bans.getBans().get(0);
        assertThat(ban.getBannedRole()).isEqualTo(Constants.ROLE_TEST);
        assertThat(ban.getPermanent()).isFalse();
        assertThat(ban.getReason()).isEqualTo(REASON);
        assertThat(ban.getBannedById()).isEqualTo(adminUserId);
        assertThat(ban.getBannedByUsername()).isEqualTo(userData.getUsername());
        assertThat(ban.getBannedByEmail()).isEqualTo(userData.getEmail());

        //Revoke ban - Blank password
        Response revokeBan_blankPasswordResponse = BanActions.getRevokeBanResponse(language, accessTokenId, ban.getId(), " ");

        ResponseValidator.verifyInvalidParam(language, revokeBan_blankPasswordResponse, "password", "must not be null or blank");

        //Revoke ban - Incorrect password
        Response revokeBan_incorrectPasswordResponse = BanActions.getRevokeBanResponse(language, accessTokenId, ban.getId(), "asd");

        ResponseValidator.verifyBadRequest(language, revokeBan_incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);

        //Revoke ban
        BanActions.revokeBan(language, accessTokenId, ban.getId(), userData.getPassword());

        bans = BanActions.getBans(language, accessTokenId, testUserId);

        assertThat(bans.getBans()).isEmpty();
    }
}
