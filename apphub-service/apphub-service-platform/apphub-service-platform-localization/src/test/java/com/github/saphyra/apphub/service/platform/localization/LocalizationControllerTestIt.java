package com.github.saphyra.apphub.service.platform.localization;

import com.github.saphyra.apphub.lib.config.Endpoints;
import com.github.saphyra.apphub.test.common.api.ApiTestConfiguration;
import com.github.saphyra.apphub.test.common.rest_assured.RequestFactory;
import com.github.saphyra.apphub.test.common.rest_assured.UrlFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@ContextConfiguration(classes = ApiTestConfiguration.class)
public class LocalizationControllerTestIt {
    @LocalServerPort
    private int serverPort;

    @Test
    public void unknownLocale() {
        assertThat(getTranslateResponse("unknown", "GENERAL_ERROR")).isEqualTo("GENERAL_ERROR could not be translated.");
    }

    @Test
    public void unknownErrorCode() {
        assertThat(getTranslateResponse("hu", "GENERAL_ERRORs")).isEqualTo("GENERAL_ERRORs could not be translated.");
    }

    @Test
    public void translate() {
        assertThat(getTranslateResponse("hu", "GENERAL_ERROR")).isEqualTo("Ismeretlen hiba.");
    }

    private String getTranslateResponse(String locale, String errorCode) {
        return RequestFactory.createRequest(locale)
            .get(UrlFactory.create(serverPort, Endpoints.TRANSLATE_ERROR_CODE.concat("?error_code=").concat(errorCode)))
            .getBody()
            .asString();
    }
}