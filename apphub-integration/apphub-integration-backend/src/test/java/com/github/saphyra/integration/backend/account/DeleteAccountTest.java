package com.github.saphyra.integration.backend.account;

import com.github.saphyra.apphub.integration.backend.BackEndTest;
import com.github.saphyra.apphub.integration.backend.ResponseValidator;
import com.github.saphyra.apphub.integration.backend.actions.AccountActions;
import com.github.saphyra.apphub.integration.common.framework.DataConstants;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.common.framework.localization.Language;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.common.framework.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.OneParamRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.UUID;

import static com.github.saphyra.apphub.integration.backend.ResponseValidator.verifyInvalidParam;
import static org.assertj.core.api.Assertions.assertThat;

public class DeleteAccountTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider")
    public void deleteAccount(Language language) {
        RegistrationParameters userData = RegistrationParameters.validParameters();
        UUID accessTokenId = IndexPageActions.registerAndLogin(language, userData);

        //Null password
        Response nullPasswordResponse = AccountActions.getDeleteAccountResponse(language, accessTokenId, new OneParamRequest<>(null));
        verifyInvalidParam(language, nullPasswordResponse, "password", "must not be null");

        //Incorrect password
        Response incorrectPasswordResponse = AccountActions.getDeleteAccountResponse(language, accessTokenId, new OneParamRequest<>(DataConstants.INCORRECT_PASSWORD));
        ResponseValidator.verifyBadRequest(language, incorrectPasswordResponse, ErrorCode.BAD_PASSWORD);

        //Successful deletion
        Response response = AccountActions.getDeleteAccountResponse(language, accessTokenId, new OneParamRequest<>(userData.getPassword()));
        assertThat(response.getStatusCode()).isEqualTo(200);
        Response loginResponse = IndexPageActions.getLoginResponse(language, userData.toLoginRequest());
        assertThat(loginResponse.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = loginResponse.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.BAD_CREDENTIALS.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.BAD_CREDENTIALS));
    }
}
