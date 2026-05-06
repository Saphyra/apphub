package com.github.saphyra.apphub.integration.framework;

import com.github.saphyra.apphub.integration.core.TestConfiguration;
import com.github.saphyra.apphub.integration.localization.Language;
import io.restassured.config.DecoderConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.http.params.CoreConnectionPNames;

import static io.restassured.RestAssured.given;
import static java.util.Objects.isNull;

public class RequestFactory {
    public static RequestSpecification createAuthorizedRequest(String accessToken, String refreshToken) {
        return createAuthorizedRequest(accessToken)
            .cookie(Constants.REFRESH_TOKEN_COOKIE, refreshToken);
    }

    public static RequestSpecification createAuthorizedRequest(String accessToken) {
        return createRequest()
            .cookie(Constants.ACCESS_TOKEN_COOKIE, accessToken);
    }

    public static RequestSpecification createRequest() {
        return createRequest(TestConfiguration.DEFAULT_LANGUAGE);
    }

    public static RequestSpecification createRequest(Language locale) {
        RequestSpecification requestSpecification = given()
            .config(
                RestAssuredConfig.config()
                    .decoderConfig(DecoderConfig.decoderConfig().contentDecoders(DecoderConfig.ContentDecoder.DEFLATE))
                    .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam(CoreConnectionPNames.CONNECTION_TIMEOUT, 10000)
                        .setParam(CoreConnectionPNames.SO_TIMEOUT, 10000)
                    )
            )
            .contentType(ContentType.JSON)
            .header("Connection", "close");
        if (!isNull(locale)) {
            requestSpecification.cookie(Constants.LOCALE_COOKIE, locale.getLocale());
        }

        if (TestConfiguration.REST_LOGGING_ENABLED) {
            requestSpecification.filter(new ResponseLoggingFilter()).log().all();
        }
        return requestSpecification;
    }

    public static RequestSpecification createRefreshRequest(String refreshToken) {
        return createRequest()
            .cookie(Constants.REFRESH_TOKEN_COOKIE, refreshToken);
    }
}
