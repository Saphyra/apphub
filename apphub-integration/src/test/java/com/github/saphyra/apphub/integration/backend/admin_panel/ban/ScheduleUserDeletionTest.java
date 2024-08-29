package com.github.saphyra.apphub.integration.backend.admin_panel.ban;

import com.github.saphyra.apphub.integration.action.backend.IndexPageActions;
import com.github.saphyra.apphub.integration.action.backend.admin_panel.BanActions;
import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Constants;
import com.github.saphyra.apphub.integration.framework.DatabaseUtil;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.ResponseValidator;
import com.github.saphyra.apphub.integration.structure.api.admin_panel.MarkUserForDeletionRequest;
import com.github.saphyra.apphub.integration.structure.api.user.BanResponse;
import com.github.saphyra.apphub.integration.structure.api.user.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduleUserDeletionTest extends BackEndTest {
    private static final LocalDate DATE = LocalDate.now()
        .plusDays(1);
    private static final Integer HOURS = 23;
    private static final Integer MINUTES = 54;
    private static final String MARKED_FOR_DELETION_AT = String.format("%sT%s:%s", DATE, HOURS, MINUTES);

    @Test(groups = {"be", "admin-panel"})
    public void scheduleUserDeletionCd() {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(getServerPort(), userData);
        DatabaseUtil.addRoleByEmail(userData.getEmail(), Constants.ROLE_ADMIN);

        RegistrationParameters testUser = RegistrationParameters.validParameters();
        IndexPageActions.registerUser(getServerPort(), testUser.toRegistrationRequest());
        UUID testUserId = DatabaseUtil.getUserIdByEmail(testUser.getEmail());

        nullPassword(accessTokenId, testUserId);
        incorrectPassword(accessTokenId, testUserId);
        nullMarkedForDeletionAt(userData, accessTokenId, testUserId);
        incorrectTime(userData, accessTokenId, testUserId);
        markUserForDeletion(userData, accessTokenId, testUserId);
        unmarkUserForDeletion(accessTokenId, testUserId);
    }

    private static void nullPassword(UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest nullPasswordRequest = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(MARKED_FOR_DELETION_AT)
            .password(null)
            .build();

        Response nullPasswordResponse = BanActions.getMarkForDeletionResponse(getServerPort(), accessTokenId, testUserId, nullPasswordRequest);

        ResponseValidator.verifyInvalidParam(nullPasswordResponse, "password", "must not be null");
    }

    private static void incorrectPassword(UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest incorrectPasswordRequest = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(MARKED_FOR_DELETION_AT)
            .password("asf")
            .build();

        Response incorrectPasswordResponse = BanActions.getMarkForDeletionResponse(getServerPort(), accessTokenId, testUserId, incorrectPasswordRequest);

        ResponseValidator.verifyBadRequest(incorrectPasswordResponse, ErrorCode.INCORRECT_PASSWORD);
    }

    private static void nullMarkedForDeletionAt(RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest nullTimeRequest = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(null)
            .password(userData.getPassword())
            .build();

        Response nullTimeResponse = BanActions.getMarkForDeletionResponse(getServerPort(), accessTokenId, testUserId, nullTimeRequest);

        ResponseValidator.verifyInvalidParam(nullTimeResponse, "markForDeletionAt", "must not be null");
    }

    private static void incorrectTime(RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest incorrectTimeRequest = MarkUserForDeletionRequest.builder()
            .markForDeletionAt("asd")
            .password(userData.getPassword())
            .build();

        Response incorrectTimeResponse = BanActions.getMarkForDeletionResponse(getServerPort(), accessTokenId, testUserId, incorrectTimeRequest);

        ResponseValidator.verifyInvalidParam(incorrectTimeResponse, "markForDeletionAt", "failed to parse");
    }

    private static void markUserForDeletion(RegistrationParameters userData, UUID accessTokenId, UUID testUserId) {
        MarkUserForDeletionRequest markUserForDeletionRequest = MarkUserForDeletionRequest.builder()
            .markForDeletionAt(MARKED_FOR_DELETION_AT)
            .password(userData.getPassword())
            .build();

        BanResponse markUserForDeletionResponse = BanActions.markUserForDeletion(getServerPort(), accessTokenId, testUserId, markUserForDeletionRequest);

        assertThat(markUserForDeletionResponse.getMarkedForDeletion()).isTrue();
        assertThat(markUserForDeletionResponse.getMarkedForDeletionAt()).isEqualTo(DATE + String.format(" %s:%s", HOURS, MINUTES));
    }

    private static void unmarkUserForDeletion(UUID accessTokenId, UUID testUserId) {
        BanResponse unmarkUserForDeletionResponse = BanActions.unmarkUserForDeletion(getServerPort(), accessTokenId, testUserId);

        assertThat(unmarkUserForDeletionResponse.getMarkedForDeletion()).isFalse();
        assertThat(unmarkUserForDeletionResponse.getMarkedForDeletionAt()).isNull();
    }
}
