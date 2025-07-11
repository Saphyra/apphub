package com.github.saphyra.apphub.integration.backend.misc;

import com.github.saphyra.apphub.integration.core.BackEndTest;
import com.github.saphyra.apphub.integration.framework.ErrorCode;
import com.github.saphyra.apphub.integration.framework.RequestFactory;
import com.github.saphyra.apphub.integration.framework.UrlFactory;
import com.github.saphyra.apphub.integration.framework.endpoints.UserEndpoints;
import com.github.saphyra.apphub.integration.structure.api.ErrorResponse;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorizationTest extends BackEndTest {
    @Test(groups = {"be", "misc"})
    public void callProtectedEndpointWithoutAccessToken() {
        Response response = RequestFactory.createRequest()
            .get(UrlFactory.create(getServerPort(), UserEndpoints.CHECK_SESSION));

        assertThat(response.getStatusCode()).isEqualTo(401);
        ErrorResponse errorResponse = response.getBody().as(ErrorResponse.class);
        assertThat(errorResponse.getErrorCode()).isEqualTo(ErrorCode.NO_SESSION_AVAILABLE.name());
    }
}
