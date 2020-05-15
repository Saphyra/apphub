package com.github.saphyra.integration.backend.modules;

import com.github.saphyra.apphub.integration.backend.actions.IndexPageActions;
import com.github.saphyra.apphub.integration.backend.actions.ModulesPageActions;
import com.github.saphyra.apphub.integration.backend.model.ModulesResponse;
import com.github.saphyra.apphub.integration.common.TestBase;
import com.github.saphyra.apphub.integration.common.framework.ErrorCode;
import com.github.saphyra.apphub.integration.common.model.ErrorResponse;
import com.github.saphyra.apphub.integration.common.model.LoginRequest;
import com.github.saphyra.apphub.integration.common.model.RegistrationParameters;
import com.github.saphyra.apphub.integration.common.model.RegistrationRequest;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class ModulesTest extends TestBase {
    @Test
    public void getModules() {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(registrationRequest);
        UUID accessTokenId = IndexPageActions.login(LoginRequest.builder().email(registrationRequest.getEmail()).password(registrationRequest.getPassword()).build());

        Map<String, List<ModulesResponse>> result = ModulesPageActions.getModules(accessTokenId);

        assertThat(result).containsOnlyKeys("accounts");
        ModulesResponse expectedModule = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(false)
            .build();
        assertThat(result.get("accounts")).containsExactly(expectedModule);
    }

    @Test
    public void setAsFavorite_unknownModule() {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(registrationRequest);
        UUID accessTokenId = IndexPageActions.login(LoginRequest.builder().email(registrationRequest.getEmail()).password(registrationRequest.getPassword()).build());

        Response response = ModulesPageActions.getSetAsFavoriteResponse(accessTokenId, "unknown-module", true);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Érvénytelen paraméter.");
        assertThat(errorResponse.getParams().get("module")).isEqualTo("does not exist");
    }

    @Test
    public void setAsFavorite_nullValue() {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(registrationRequest);
        UUID accessTokenId = IndexPageActions.login(LoginRequest.builder().email(registrationRequest.getEmail()).password(registrationRequest.getPassword()).build());

        Response response = ModulesPageActions.getSetAsFavoriteResponse(accessTokenId, "account", null);

        assertThat(response.getStatusCode()).isEqualTo(400);

        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getHttpStatus()).isEqualTo(400);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo("Érvénytelen paraméter.");
        assertThat(errorResponse.getParams().get("value")).isEqualTo("must not be null");
    }

    @Test
    public void setAsFavorite() {
        RegistrationRequest registrationRequest = RegistrationParameters.validParameters()
            .toRegistrationRequest();
        IndexPageActions.registerUser(registrationRequest);
        UUID accessTokenId = IndexPageActions.login(LoginRequest.builder().email(registrationRequest.getEmail()).password(registrationRequest.getPassword()).build());

        Map<String, List<ModulesResponse>> result = ModulesPageActions.setAsFavorite(accessTokenId, "account", true);

        assertThat(result).containsOnlyKeys("accounts");
        ModulesResponse expectedModule = ModulesResponse.builder()
            .name("account")
            .url("/web/user/account")
            .favorite(true)
            .build();
        assertThat(result.get("accounts")).containsExactly(expectedModule);
    }
}
