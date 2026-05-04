package com.github.saphyra.apphub.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.authorization.TokenResponse;
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

    @Test(groups = {"be", "admin-panel"})
    public void banCrud() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), userData.toRegistrationRequest());
        UUID adminUserId = DatabaseUtil.getUserIdByEmail(userData.getEmail());
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);
        TokenResponse tokenResponse = IndexPageActions.login(getServerPort(), userData.toLoginRequest());
        String accessToken = tokenResponse.getAccessToken()
            .getJwt();

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        ban_nullBannedUserId(userData, accessToken);
        ban_blankBannedRole(userData, accessToken, testUserId);
        ban_nullPermanent(userData, accessToken, testUserId);
        ban_blankReason(userData, accessToken, testUserId);
        ban_blankPassword(accessToken, testUserId);
        ban_nullDuration(userData, accessToken, testUserId);
        ban_durationTooLow(userData, accessToken, testUserId);
        ban_unknownChronoUnit(userData, accessToken, testUserId);
        accessToken = ban_incorrectPassword(userData, accessToken, testUserId);
        ban(userData, accessToken, testUserId);
        BanDetailsResponse ban = getBans(userData, accessToken, adminUserId, testUser, testUserId);
        revokeBan_blankPassword(accessToken, ban);
        revokeBan_incorrectPassword(userData, accessToken, testUserId, ban);
    }

    private static void ban_nullBannedUserId(RegistrationParameters userData, String accessToken) {
        BanRequest ban_nullBannedUserIdRequest = BanRequest.builder()
            .bannedUserId(null)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_nullBannedUserIdResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_nullBannedUserIdRequest);

        ResponseValidator.verifyInvalidParam(ban_nullBannedUserIdResponse, "bannedUserId", "must not be null");
    }

    private static void ban_blankBannedRole(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_blankBannedRoleRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(" ")
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_blankBannedRoleResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_blankBannedRoleRequest);

        ResponseValidator.verifyInvalidParam(ban_blankBannedRoleResponse, "bannedRole", "must not be null or blank");
    }

    private static void ban_nullPermanent(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_nullPermanentRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(null)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_nullPermanentResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_nullPermanentRequest);

        ResponseValidator.verifyInvalidParam(ban_nullPermanentResponse, "permanent", "must not be null");
    }

    private static void ban_blankReason(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_blankReasonRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(" ")
            .password(userData.getPassword())
            .build();

        Response ban_blankReasonResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_blankReasonRequest);

        ResponseValidator.verifyInvalidParam(ban_blankReasonResponse, "reason", "must not be null or blank");
    }

    private static void ban_blankPassword(String accessToken, UUID testUserId) {
        BanRequest ban_blankPasswordRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(" ")
            .build();

        Response ban_blankPasswordResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_blankPasswordRequest);

        ResponseValidator.verifyInvalidParam(ban_blankPasswordResponse, "password", "must not be null or blank");
    }

    private static void ban_nullDuration(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_nullDurationRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(null)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_nullDurationResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_nullDurationRequest);

        ResponseValidator.verifyInvalidParam(ban_nullDurationResponse, "duration", "must not be null");
    }

    private static void ban_durationTooLow(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_durationTooLowRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(0)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_durationTooLowResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_durationTooLowRequest);

        ResponseValidator.verifyInvalidParam(ban_durationTooLowResponse, "duration", "too low");
    }

    private static void ban_unknownChronoUnit(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_unknownChronoUnitRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit("asd")
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        Response ban_unknownChronoUnitResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_unknownChronoUnitRequest);

        ResponseValidator.verifyInvalidParam(ban_unknownChronoUnitResponse, "chronoUnit", "invalid value");
    }

    private static String ban_incorrectPassword(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest ban_incorrectPasswordRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password("asd")
            .build();

        String ati = accessToken;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(_ -> {
                Response ban_incorrectPasswordResponse = BanActions.getBanResponse(getServerPort(), ati, ban_incorrectPasswordRequest);

                ResponseValidator.verifyBadRequest(ban_incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response ban_accountLockedResponse = BanActions.getBanResponse(getServerPort(), accessToken, ban_incorrectPasswordRequest);
        verifyErrorResponse(ban_accountLockedResponse, 423, ErrorCode.ACCOUNT_LOCKED);


        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();
        return accessToken;
    }

    private static void ban(RegistrationParameters userData, String accessToken, UUID testUserId) {
        BanRequest banRequest = BanRequest.builder()
            .bannedUserId(testUserId)
            .bannedRole(Constants.ROLE_TEST)
            .permanent(false)
            .duration(1)
            .chronoUnit(ChronoUnit.MINUTES.name())
            .reason(REASON)
            .password(userData.getPassword())
            .build();

        BanActions.ban(getServerPort(), accessToken, banRequest);
    }

    private static BanDetailsResponse getBans(RegistrationParameters userData, String accessToken, UUID adminUserId, RegistrationParameters testUser, UUID testUserId) {
        BanResponse bans = BanActions.getBans(getServerPort(), accessToken, testUserId);

        assertThat(bans.getUserId()).isEqualTo(testUserId);
        assertThat(bans.getEmail()).isEqualTo(testUser.getEmail());
        assertThat(bans.getUsername()).isEqualTo(testUser.getUsername());
        assertThat(bans.getBans()).hasSize(1);
        BanDetailsResponse ban = bans.getBans().getFirst();
        assertThat(ban.getBannedRole()).isEqualTo(Constants.ROLE_TEST);
        assertThat(ban.getPermanent()).isFalse();
        assertThat(ban.getReason()).isEqualTo(REASON);
        assertThat(ban.getBannedById()).isEqualTo(adminUserId);
        assertThat(ban.getBannedByUsername()).isEqualTo(userData.getUsername());
        assertThat(ban.getBannedByEmail()).isEqualTo(userData.getEmail());
        return ban;
    }

    private static void revokeBan_blankPassword(String accessToken, BanDetailsResponse ban) {
        Response revokeBan_blankPasswordResponse = BanActions.getRevokeBanResponse(getServerPort(), accessToken, ban.getId(), " ");

        ResponseValidator.verifyInvalidParam(revokeBan_blankPasswordResponse, "password", "must not be null or blank");
    }

    private static void revokeBan_incorrectPassword(RegistrationParameters userData, String accessToken, UUID testUserId, BanDetailsResponse ban) {
        BanResponse bans;
        String ati2 = accessToken;
        Stream.generate(() -> "")
            .limit(2)
            .forEach(_ -> {
                Response revokeBan_incorrectPasswordResponse = BanActions.getRevokeBanResponse(getServerPort(), ati2, ban.getId(), "asd");
                ResponseValidator.verifyBadRequest(revokeBan_incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
            });

        Response revokeBan_accountLockedResponse = BanActions.getRevokeBanResponse(getServerPort(), ati2, ban.getId(), "asd");
        verifyErrorResponse(revokeBan_accountLockedResponse, 423, ErrorCode.ACCOUNT_LOCKED);

        DatabaseUtil.unlockUserByEmail(userData.getEmail());
        accessToken = IndexPageActions.login(getServerPort(), userData.toLoginRequest())
            .getAccessToken()
            .getJwt();

        //Revoke ban
        BanActions.revokeBan(getServerPort(), accessToken, ban.getId(), userData.getPassword());

        bans = BanActions.getBans(getServerPort(), accessToken, testUserId);

        assertThat(bans.getBans()).isEmpty();
    }
}
