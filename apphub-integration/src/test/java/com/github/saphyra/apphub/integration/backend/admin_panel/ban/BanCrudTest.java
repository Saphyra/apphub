package com.github.saphyra.apphub.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.user.BanDetailsResponse;
import com.github.saphyra.apphub.integration.structure.api.user.BanRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.saphyra.apphub.integration.framework.ResponseValidator.verifyErrorResponse;
import static org.assertj.core.api.Assertions.assertThat;

public class BanCrudTest extends BackEndTest {
    private static final String REASON = "reason";

    @Test(dataProvider = "languageDataProvider", groups = {"be", "admin-panel"})
    public void banCrud(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        UUID adminUserId = DatabaseUtil.getUserIdByEmail(userData.getEmail());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        ban_nullBannedUserId(language, userData, accessTokenId);
        ban_blankBannedRole(language, userData, accessTokenId, testUserId);
        ban_nullPermanent(language, userData, accessTokenId, testUserId);
        ban_blankReason(language, userData, accessTokenId, testUserId);
        ban_blankPassword(language, accessTokenId, testUserId);
        ban_nullDuration(language, userData, accessTokenId, testUserId);
        ban_durationTooLow(language, userData, accessTokenId, testUserId);
        ban_unknownChronoUnit(language, userData, accessTokenId, testUserId);
        accessTokenId = ban_incorrectPassword(language, userData, accessTokenId, testUserId);
        ban(language, userData, accessTokenId, testUserId);
        BanDetailsResponse ban = getBans(language, userData, accessTokenId, adminUserId, testUser, testUserId);
        revokeBan_blankPassword(language, accessTokenId, ban);
        revokeBan_incorrectPassword(language, userData, accessTokenId, testUserId, ban);
    }

    private static void ban_nullBannedUserId(Language language, RegistrationParameters userData, UUID accessTokenId) {
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
    }

    private static void ban_blankBannedRole(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static void ban_nullPermanent(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static void ban_blankReason(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static void ban_blankPassword(Language language, UUID accessTokenId, UUID testUserId) {
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
    }

    private static void ban_nullDuration(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static void ban_durationTooLow(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static void ban_unknownChronoUnit(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static UUID ban_incorrectPassword(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        BanRequest ban_incorrectPasswordRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password("asd")
            .build();

        UUID ati = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response ban_incorrectPasswordResponse = BanActions.getBanResponse(language, ati, ban_incorrectPasswordRequest);

                ResponseValidator.verifyBadRequest(language, ban_incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response ban_accountLockedResponse = BanActions.getBanResponse(language, accessTokenId, ban_incorrectPasswordRequest);
        verifyErrorResponse(language, ban_accountLockedResponse, 401, ErrorCode.ACCOUNT_LOCKED);


        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(language, userData.toLoginRequest());
        return accessTokenId;
    }

    private static void ban(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
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
    }

    private static BanDetailsResponse getBans(Language language, RegistrationParameters userData, UUID accessTokenId, UUID adminUserId, RegistrationParameters testUser, UUID testUserId) {
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
        return ban;
    }

    private static void revokeBan_blankPassword(Language language, UUID accessTokenId, BanDetailsResponse ban) {
        Response revokeBan_blankPasswordResponse = BanActions.getRevokeBanResponse(language, accessTokenId, ban.getId(), " ");

        ResponseValidator.verifyInvalidParam(language, revokeBan_blankPasswordResponse, "password", "must not be null or blank");
    }

    private static void revokeBan_incorrectPassword(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId, BanDetailsResponse ban) {
        BanResponse bans;
        UUID ati2 = accessTokenId;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(s -> {
                Response revokeBan_incorrectPasswordResponse = BanActions.getRevokeBanResponse(language, ati2, ban.getId(), "asd");
                ResponseValidator.verifyBadRequest(language, revokeBan_incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response revokeBan_accountLockedResponse = BanActions.getRevokeBanResponse(language, ati2, ban.getId(), "asd");
        verifyErrorResponse(language, revokeBan_accountLockedResponse, 401, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessTokenId = IndexPageActions.login(language, userData.toLoginRequest());

        //Revoke ban
        BanActions.revokeBan(language, accessTokenId, ban.getId(), userData.getPassword());

        bans = BanActions.getBans(language, accessTokenId, testUserId);

        assertThat(bans.getBans()).isEmpty();
    }
}
