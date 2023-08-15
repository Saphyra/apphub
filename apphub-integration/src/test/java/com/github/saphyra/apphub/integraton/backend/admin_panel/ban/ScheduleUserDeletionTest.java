package com.github.saphyra.apphub.integraton.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleUserDeletionTest extends BackEndTest {
    private static final LocalDate DATE = LocalDate.now()
        .plusDays(1);
    private static final Integer HOURS = 23;
    private static final Integer MINUTES = 54;
    private static final String TIME = HOURS + ":" + MINUTES;

    @Test(dataProvider = "languageDataProvider", groups = {"be", "admin-panel"})
    public void scheduleUserDeletionCd(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(language, testUser.toRegistrationRequest());
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullPassword(language, accessTokenId, testUserId);
        incorrectPassword(language, accessTokenId, testUserId);
        nullDate(language, userData, accessTokenId, testUserId);
        nullTime(language, userData, accessTokenId, testUserId);
        incorrectTime(language, userData, accessTokenId, testUserId);
        markUserForDeletion(language, userData, accessTokenId, testUserId);
        unmarkUserForDeletion(language, accessTokenId, testUserId);
    }

    private static void nullPassword(Language language, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest nullPasswordRequest = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(TIME)
            .password(null)
            .build();

        Response nullPasswordResponse = BanActions.getMarkForDeletionResponse(language, accessTokenId, testUserId, nullPasswordRequest);

        ResponseValidator.verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(Language language, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest incorrectPasswordRequest = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(TIME)
            .password("asf")
            .build();

        Response incorrectPasswordResponse = BanActions.getMarkForDeletionResponse(language, accessTokenId, testUserId, incorrectPasswordRequest);

        ResponseValidator.verifyBadRequest(language, incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void nullDate(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest nullDateRequest = MarkUserForDeletionRequest.builder()
            .date(null)
            .time(TIME)
            .password(userData.getPassword())
            .build();

        Response nullDateResponse = BanActions.getMarkForDeletionResponse(language, accessTokenId, testUserId, nullDateRequest);

        ResponseValidator.verifyInvalidParam(language, nullDateResponse, "date", "must not be null");
    }

    private static void nullTime(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest nullTimeRequest = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(null)
            .password(userData.getPassword())
            .build();

        Response nullTimeResponse = BanActions.getMarkForDeletionResponse(language, accessTokenId, testUserId, nullTimeRequest);

        ResponseValidator.verifyInvalidParam(language, nullTimeResponse, "time", "must not be null");
    }

    private static void incorrectTime(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest incorrectTimeRequest = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time("asd")
            .password(userData.getPassword())
            .build();

        Response incorrectTimeResponse = BanActions.getMarkForDeletionResponse(language, accessTokenId, testUserId, incorrectTimeRequest);

        ResponseValidator.verifyInvalidParam(language, incorrectTimeResponse, "time", "invalid value");
    }

    private static void markUserForDeletion(Language language, RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest markUserForDeletionRequest = MarkUserForDeletionRequest.builder()
            .date(DATE)
            .time(TIME)
            .password(userData.getPassword())
            .build();

        BanResponse markUserForDeletionResponse = BanActions.markUserForDeletion(language, accessTokenId, testUserId, markUserForDeletionRequest);

        assertThat(markUserForDeletionResponse.getMarkedForDeletion()).isTrue();
        assertThat(markUserForDeletionResponse.getMarkedForDeletionAt()).isEqualTo(LocalDateTime.of(DATE, LocalTime.of(HOURS, MINUTES, 0)).toEpochSecond(ZoneOffset.UTC));
    }

    private static void unmarkUserForDeletion(Language language, UUID accessTokenId, UUID testUserId) {
        BanResponse unmarkUserForDeletionResponse = BanActions.unmarkUserForDeletion(language, accessTokenId, testUserId);

        assertThat(unmarkUserForDeletionResponse.getMarkedForDeletion()).isFalse();
        assertThat(unmarkUserForDeletionResponse.getMarkedForDeletionAt()).isNull();
    }
}
