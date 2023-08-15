package com.github.saphyra.apphub.integraton.backend;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.Endpoints;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.localization.Language;
import com.github.saphyra.apphub.integration.localization.LocalizationKey;
import com.github.saphyra.apphub.integration.localization.LocalizationProperties;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTest extends BackEndTest {
    @Test(dataProvider = "languageDataProvider", groups = {"be", "misc"})
    public void callProtectedEndpointWithoutAccessToken(Language language) {
        Response response = RequestFactory.createRequest(language)
            .get(UrlFactory.create(Endpoints.CHECK_SESSION));

        assertThat(response.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
        assertThat(errorResponse.getLocalizedMessage()).isEqualTo(LocalizationProperties.getProperty(language, LocalizationKey.SESSION_EXPIRED));
    }
}
